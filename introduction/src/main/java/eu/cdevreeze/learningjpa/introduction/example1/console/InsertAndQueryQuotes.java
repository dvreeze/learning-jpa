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
import eu.cdevreeze.learningjpa.introduction.example1.entity.Author;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote_;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Subject;
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

/**
 * Example program adding and querying for quotes. It uses an H2 in-memory database, and does all the
 * needed bootstrapping itself, without needing any context.
 *
 * @author Chris de Vreeze
 */
public class InsertAndQueryQuotes {

    private static final String LOAD_GRAPH = "jakarta.persistence.loadgraph";

    public static void main(String[] args) {
        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            // Below, each call to method "callInTransaction" creates a new application-managed
            // EntityManager. Also, a new resource-local transaction is started, and the new EntityManager
            // is associated with that new transaction. If we had used JTA transactions, that would not have been the case.

            ImmutableList<Model.Quote> insertedQuotes =
                    emf.callInTransaction(em ->
                            insertQuotes(em).stream().map(Quote::toModel).collect(ImmutableList.toImmutableList()));

            // JPQL query for quotes (using a Criteria query), without first adding a new Quote
            ImmutableList<Model.Quote> queriedQuotes =
                    emf.callInTransaction(InsertAndQueryQuotes::findAllQuotes);

            Preconditions.checkArgument(queriedQuotes.equals(insertedQuotes));

            // Persist new quotes, and then query for all quotes
            ImmutableList<Model.Quote> quotesWithExtraQuote =
                    emf.callInTransaction(InsertAndQueryQuotes::insertQuotesThenFindAllQuotes);

            Preconditions.checkArgument(quotesWithExtraQuote.containsAll(insertedQuotes));
            Preconditions.checkArgument(quotesWithExtraQuote.size() == 2 + insertedQuotes.size());

            quotesWithExtraQuote.forEach(qt -> {
                System.out.println();
                System.out.println(qt);
            });

            System.out.println();
            System.out.printf("Number of quotes: %d%n", quotesWithExtraQuote.size());
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        return QuotesEntityManagerFactoryCreator.createEntityManagerFactory();
    }

    private static ImmutableList<Model.Quote> insertQuotesThenFindAllQuotes(EntityManager entityManager) {
        persistSomeQuotes(entityManager);

        return findAllQuotes(entityManager);
    }

    private static void persistSomeQuotes(EntityManager entityManager) {
        // Upserting the hard way. We configured no cascading behaviour.
        Author davidIcke = upsertAuthor("David Icke", entityManager);
        Subject tyranny = upsertSubject("tyranny", entityManager);
        Subject truth = upsertSubject("truth", entityManager);

        entityManager.persist(new Quote(
                "Scarcity equals dependency equals control",
                davidIcke,
                List.of(tyranny)
        ));
        entityManager.flush(); // Flushing this time
        entityManager.persist(new Quote(
                "Infinite love is the only truth. Everything else is illusion.",
                davidIcke,
                List.of(truth)
        ));
        // Not flushing this time. The JPA EntityManager should do the flushing itself automatically.
    }

    private static Author upsertAuthor(String name, EntityManager entityManager) {
        // Upserting the hard way. We configured no cascading behaviour.
        Optional<Author> authorOption =
                entityManager.createQuery("select auth from Author auth where auth.name = ?1", Author.class)
                        .setParameter(1, name)
                        .getResultStream()
                        .findFirst();
        return authorOption.orElseGet(() -> {
            Author auth = new Author(name);
            entityManager.persist(auth);
            return auth;
        });
    }

    private static Subject upsertSubject(String subject, EntityManager entityManager) {
        // Upserting the hard way. We configured no cascading behaviour.
        Optional<Subject> subjectOption =
                entityManager.createQuery("select subj from Subject subj where subj.subject = ?1", Subject.class)
                        .setParameter(1, subject)
                        .getResultStream()
                        .findFirst();
        return subjectOption.orElseGet(() -> {
            Subject subj = new Subject(subject);
            entityManager.persist(subj);
            return subj;
        });
    }

    private static ImmutableList<Model.Quote> findAllQuotes(EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> cq = cb.createQuery(Quote.class);

        Root<Quote> quote = cq.from(Quote.class);
        cq.select(quote);

        EntityGraph<Quote> quoteGraph = entityManager.createEntityGraph(Quote.class);
        quoteGraph.addSubgraph(Quote_.attributedTo);
        quoteGraph.addElementSubgraph(Quote_.subjects);

        return entityManager.createQuery(cq)
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
