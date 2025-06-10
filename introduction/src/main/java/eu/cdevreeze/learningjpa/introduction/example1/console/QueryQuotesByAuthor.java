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
import eu.cdevreeze.learningjpa.introduction.example1.entity.Author_;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote_;
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Objects;

/**
 * Example program querying for quotes by a given author. It uses an H2 in-memory database, and does all the
 * needed bootstrapping itself, without needing any context.
 *
 * @author Chris de Vreeze
 */
public class QueryQuotesByAuthor {

    private static final String LOAD_GRAPH = "jakarta.persistence.loadgraph";

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);
        String authorName = args[0];

        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            // Below, each call to method "callInTransaction" creates a new application-managed
            // EntityManager. Also, a new resource-local transaction is started, and the new EntityManager
            // is associated with that new transaction. If we had used JTA transactions, that would not have been the case.

            ImmutableList<Model.Quote> insertedQuotes =
                    emf.callInTransaction(em ->
                            insertQuotes(em).stream().map(Quote::toModel).collect(ImmutableList.toImmutableList()));
            ImmutableList<Model.Quote> filteredQuotes =
                    insertedQuotes.stream()
                            .filter(qt -> qt.attributedTo().name().equals(authorName))
                            .collect(ImmutableList.toImmutableList());

            // JPQL query for quotes of the author, using a JPQL query string
            ImmutableList<Model.Quote> queriedQuotes =
                    emf.callInTransaction(em -> findQuotesByAuthor(em, authorName));

            Preconditions.checkArgument(queriedQuotes.equals(filteredQuotes));

            // The same query, as criteria API query, depending on the generated metamodel
            ImmutableList<Model.Quote> queriedQuotesUsingCriteriaApi =
                    emf.callInTransaction(em -> findQuotesByAuthorUsingCriteriaApi(em, authorName));

            Preconditions.checkArgument(queriedQuotesUsingCriteriaApi.equals(filteredQuotes));

            // Simple query, using "load graph hint"
            ImmutableList<Model.Quote> queriedQuotesUsingGraphHint =
                    emf.callInTransaction(em -> findQuotesByAuthorUsingEntityGraph(em, authorName));

            Preconditions.checkArgument(queriedQuotesUsingGraphHint.equals(filteredQuotes));

            // Simple Criteria query, using "load graph hint"
            ImmutableList<Model.Quote> queriedQuotesUsingCriteriaApiAndGraphHint =
                    emf.callInTransaction(em -> findQuotesByAuthorUsingCriteriaApiAndEntityGraph(em, authorName));

            Preconditions.checkArgument(queriedQuotesUsingCriteriaApiAndGraphHint.equals(filteredQuotes));

            // Low level SQL-like JPQL query, using "load graph hint"
            ImmutableList<Model.Quote> queriedQuotesAtLowLevelUsingGraphHint =
                    emf.callInTransaction(em -> findQuotesByAuthorVerboselyUsingEntityGraph(em, authorName));

            Preconditions.checkArgument(queriedQuotesAtLowLevelUsingGraphHint.equals(filteredQuotes));

            queriedQuotes.forEach(qt -> {
                System.out.println();
                System.out.println(qt);
            });

            System.out.println();
            System.out.printf("Number of quotes attributed to '%s': %d%n", authorName, queriedQuotes.size());
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        return QuotesEntityManagerFactoryCreator.createEntityManagerFactory();
    }

    private static ImmutableList<Model.Quote> findQuotesByAuthor(EntityManager entityManager, String authorName) {
        // Without the "join fetch", separate SQL queries would be generated per Quote, once the associated data is lazily loaded.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        String ql = """
                select qt from Quote qt
                join fetch qt.attributedTo
                left join fetch qt.subjects
                where qt.attributedTo.name = :authorName""";
        return entityManager.createQuery(ql, Quote.class)
                .setParameter("authorName", authorName)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesByAuthorUsingCriteriaApi(EntityManager entityManager, String authorName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> cq = cb.createQuery(Quote.class);

        // Note that the Criteria API is not a "functional API" but depends on in-place mutations of objects such as CriteriaQuery.

        // Without the "join fetch", separate SQL queries would be generated per Quote, once the associated data is lazily loaded.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        Root<Quote> quote = cq.from(Quote.class);
        quote.fetch(Quote_.attributedTo, JoinType.INNER);
        quote.fetch(Quote_.subjects, JoinType.LEFT);

        cq.where(cb.equal(quote.get(Quote_.attributedTo).get(Author_.name), cb.parameter(String.class, "authName")));
        cq.select(quote);

        // Below, we could have done without the parameter, by using "cb.literal" instead of "cb.parameter" above.
        return entityManager.createQuery(cq)
                .setParameter("authName", authorName)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesByAuthorUsingEntityGraph(EntityManager entityManager, String authorName) {
        // See https://www.baeldung.com/jpa-entity-graph

        String ql = "select qt from Quote qt where qt.attributedTo.name = :authorName";

        EntityGraph<Quote> quoteGraph = entityManager.createEntityGraph(Quote.class);
        quoteGraph.addSubgraph(Quote_.attributedTo);
        quoteGraph.addElementSubgraph(Quote_.subjects);

        return entityManager.createQuery(ql, Quote.class)
                .setParameter("authorName", authorName)
                .setHint(LOAD_GRAPH, quoteGraph)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesByAuthorUsingCriteriaApiAndEntityGraph(EntityManager entityManager, String authorName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> cq = cb.createQuery(Quote.class);

        Root<Quote> quote = cq.from(Quote.class);

        cq.where(cb.equal(quote.get(Quote_.attributedTo).get(Author_.name), cb.parameter(String.class, "authName")));
        cq.select(quote);

        EntityGraph<Quote> quoteGraph = entityManager.createEntityGraph(Quote.class);
        quoteGraph.addSubgraph(Quote_.attributedTo);
        quoteGraph.addElementSubgraph(Quote_.subjects);

        return entityManager.createQuery(cq)
                .setParameter("authName", authorName)
                .setHint(LOAD_GRAPH, quoteGraph)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesByAuthorVerboselyUsingEntityGraph(EntityManager entityManager, String authorName) {
        // See https://www.baeldung.com/jpa-entity-graph

        // This is a verbose low level JPQL query that is much closer to the generated native SQL

        String ql = """
                select qt from Quote qt
                join Author auth on (qt.attributedTo.id = auth.id)
                where auth.name = :authorName""";

        EntityGraph<Quote> quoteGraph = entityManager.createEntityGraph(Quote.class);
        quoteGraph.addSubgraph(Quote_.attributedTo);
        quoteGraph.addElementSubgraph(Quote_.subjects);

        return entityManager.createQuery(ql, Quote.class)
                .setParameter("authorName", authorName)
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
