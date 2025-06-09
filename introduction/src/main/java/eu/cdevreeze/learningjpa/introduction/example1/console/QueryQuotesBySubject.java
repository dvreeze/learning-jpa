/*
 * Copyright 2025-2025 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.learningjpa.introduction.example1.console;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote_;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Subject;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Subject_;
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.Objects;

/**
 * Example program querying for quotes for a given subject. It uses an H2 in-memory database, and does all the
 * needed bootstrapping itself, without needing any context.
 *
 * @author Chris de Vreeze
 */
public class QueryQuotesBySubject {

    private static final String LOAD_GRAPH = "jakarta.persistence.loadgraph";

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);
        String subject = args[0];

        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            // Below, each call to method "callInTransaction" creates a new application-managed
            // EntityManager. Also, a new resource-local transaction is started, and the new EntityManager
            // is associated with that new transaction. If we had used JTA transactions, that would not have been the case.

            ImmutableList<Model.Quote> insertedQuotes =
                    emf.callInTransaction(em ->
                            insertQuotes(em).stream().map(Quote::toModel).collect(ImmutableList.toImmutableList()));
            ImmutableList<Model.Quote> filteredQuotes =
                    insertedQuotes.stream()
                            .filter(qt -> qt.subjects().stream().map(Model.Subject::subject).toList().contains(subject))
                            .collect(ImmutableList.toImmutableList());

            // JPQL query for quotes for the subject, using a JPQL query string
            ImmutableList<Model.Quote> queriedQuotes =
                    emf.callInTransaction(em -> findQuotesBySubject(em, subject));

            Preconditions.checkArgument(queriedQuotes.equals(filteredQuotes));

            // The same query, as criteria API query, depending on the generated metamodel
            ImmutableList<Model.Quote> queriedQuotesUsingCriteriaApi =
                    emf.callInTransaction(em -> findQuotesBySubjectUsingCriteriaApi(em, subject));

            Preconditions.checkArgument(queriedQuotesUsingCriteriaApi.equals(filteredQuotes));

            // Simple query, using "load graph hint"
            ImmutableList<Model.Quote> queriedQuotesUsingGraphHint =
                    emf.callInTransaction(em -> findQuotesBySubjectUsingEntityGraph(em, subject));

            Preconditions.checkArgument(queriedQuotesUsingGraphHint.equals(filteredQuotes));

            queriedQuotes.forEach(qt -> {
                System.out.println();
                System.out.println(qt);
            });

            System.out.println();
            System.out.printf("Number of quotes for subject '%s': %d%n", subject, queriedQuotes.size());
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        return QuotesEntityManagerFactoryCreator.createEntityManagerFactory();
    }

    private static ImmutableList<Model.Quote> findQuotesBySubject(EntityManager entityManager, String subject) {
        // Without the "join fetch", separate SQL queries would be generated per Quote.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        // Yet note that for "join fetch" we cannot use any identification variable. Hence, the join duplication.
        // It is probably better to distinguish between the query and the fetching side effect, by passing a "load graph hint".
        // In that case we could simplify the JPQL query, by just using an identification variable for the subjects.
        String ql = """
                select qt from Quote qt
                join fetch qt.attributedTo
                left join fetch qt.subjects
                left join qt.subjects subj
                where subj.subject = :subject""";
        return entityManager.createQuery(ql, Quote.class)
                .setParameter("subject", subject)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesBySubjectUsingCriteriaApi(EntityManager entityManager, String subject) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> cq = cb.createQuery(Quote.class);

        // Without the "join fetch", separate SQL queries would be generated per Quote.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        Root<Quote> quote = cq.from(Quote.class);
        quote.fetch(Quote_.attributedTo, JoinType.INNER);
        quote.fetch(Quote_.subjects, JoinType.LEFT);
        Join<Quote, Subject> quoteSubject = quote.join(Quote_.subjects, JoinType.LEFT);

        cq.where(cb.equal(quoteSubject.get(Subject_.SUBJECT), cb.parameter(String.class, Subject_.SUBJECT)));
        cq.select(quote);

        // Below, we could have done without the parameter, by using "cb.literal" instead of "cb.parameter" above.
        return entityManager.createQuery(cq)
                .setParameter("subject", subject)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesBySubjectUsingEntityGraph(EntityManager entityManager, String subject) {
        // See https://www.baeldung.com/jpa-entity-graph

        // This JPQL query is quite simple, due to the use of identification variable "subj".
        // Clearly, "qt.subjects" is a collection of subject entities reachable from Quote "qt".
        // Yet, the identification variable "subj" refers to ANY subject in that collection.
        // Note that making the distinction between the query without side effects and the "graph fetching hint" makes the query simpler.
        String ql = """
                select qt from Quote qt
                left join qt.subjects subj
                where subj.subject = :subject""";

        EntityGraph<Quote> quoteGraph = entityManager.createEntityGraph(Quote.class);
        quoteGraph.addSubgraph(Quote_.attributedTo);
        quoteGraph.addElementSubgraph(Quote_.subjects);

        return entityManager.createQuery(ql, Quote.class)
                .setParameter("subject", subject)
                .setHint(LOAD_GRAPH, quoteGraph)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static List<Quote> insertQuotes(EntityManager entityManager) {
        QuotesInserter quotesInserter = new QuotesInserter(entityManager);
        return quotesInserter.insertQuotes();
    }
}
