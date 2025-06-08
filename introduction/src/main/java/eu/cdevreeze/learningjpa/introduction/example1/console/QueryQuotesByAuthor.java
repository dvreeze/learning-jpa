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
import eu.cdevreeze.learningjpa.introduction.example1.entity.Author_;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote_;
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.Objects;

/**
 * Example program querying for quotes by a given author. It uses an H2 in-memory database, and does all the
 * needed bootstrapping itself, without needing any context.
 *
 * @author Chris de Vreeze
 */
public class QueryQuotesByAuthor {

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
        // Without the "join fetch", separate SQL queries would be generated per Quote.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        String ql = """
                select qt from Quote qt
                left join fetch qt.attributedTo auth
                left join fetch qt.subjects subj
                where auth.name = :authorName""";
        return entityManager.createQuery(ql, Quote.class)
                .setParameter("authorName", authorName)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesByAuthorUsingCriteriaApi(EntityManager entityManager, String authorName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> cq = cb.createQuery(Quote.class);

        // Without the "join fetch", separate SQL queries would be generated per Quote.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        Root<Quote> quote = cq.from(Quote.class);
        Fetch<Quote, Author> attributedTo = quote.fetch(Quote_.attributedTo, JoinType.LEFT);
        quote.fetch(Quote_.subjects, JoinType.LEFT);
        cq.select(quote)
                .where(cb.equal(quote.get(Quote_.attributedTo).get(Author_.name), cb.literal(authorName)));

        return entityManager.createQuery(cq)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static List<Quote> insertQuotes(EntityManager entityManager) {
        QuotesInserter quotesInserter = new QuotesInserter(entityManager);
        return quotesInserter.insertQuotes();
    }
}
