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
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Example program querying for quotes. It uses an H2 in-memory database, and does all the
 * needed bootstrapping itself, without needing any context.
 *
 * @author Chris de Vreeze
 */
public class QueryQuotes {

    public static void main(String[] args) {
        try (EntityManagerFactory emf = createEntityManagerFactory()) {
            // Below, each call to method "callInTransaction" creates a new application-managed
            // EntityManager. Also, a new resource-local transaction is started, and the new EntityManager
            // is associated with that new transaction. If we had used JTA transactions, that would not have been the case.

            ImmutableList<Model.Quote> insertedQuotes =
                    emf.callInTransaction(em ->
                            insertQuotes(em).stream().map(Quote::toModel).collect(ImmutableList.toImmutableList()));

            // JPQL query for quotes, using a JPQL query string
            ImmutableList<Model.Quote> queriedQuotes =
                    emf.callInTransaction(QueryQuotes::findAllQuotes);

            Preconditions.checkArgument(queriedQuotes.equals(insertedQuotes));

            // The same query, as criteria API query, depending on the generated metamodel
            ImmutableList<Model.Quote> queriedQuotesUsingCriteriaApi =
                    emf.callInTransaction(QueryQuotes::findAllQuotesUsingCriteriaApi);

            Preconditions.checkArgument(queriedQuotesUsingCriteriaApi.equals(insertedQuotes));

            // The same query, as a combination of a native query for IDs, and EntityManager.find calls
            // Of course this is very inefficient, and should not be done in practice
            ImmutableList<Model.Quote> queriedQuotesWithoutUsingJpql =
                    emf.callInTransaction(QueryQuotes::findQuotesOneByOne);

            Preconditions.checkArgument(queriedQuotesWithoutUsingJpql.equals(insertedQuotes));

            queriedQuotes.forEach(qt -> {
                System.out.println();
                System.out.println(qt);
            });

            System.out.println();
            System.out.printf("Number of quotes: %d%n", queriedQuotes.size());
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        return QuotesEntityManagerFactoryCreator.createEntityManagerFactory();
    }

    private static ImmutableList<Model.Quote> findAllQuotes(EntityManager entityManager) {
        // Without the "join fetch", separate SQL queries would be generated per Quote.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        String ql = """
                select qt from Quote qt
                left join fetch qt.attributedTo auth
                left join fetch qt.subjects subj""";
        return entityManager.createQuery(ql, Quote.class)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findAllQuotesUsingCriteriaApi(EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> cq = cb.createQuery(Quote.class);

        // Without the "join fetch", separate SQL queries would be generated per Quote.
        // Clearly that would be quite undesirable.
        // The "join fetch" does what it says, namely retrieving the quote's author and subjects as well.
        Root<Quote> quote = cq.from(Quote.class);
        quote.fetch(Quote_.attributedTo, JoinType.LEFT);
        quote.fetch(Quote_.subjects, JoinType.LEFT);
        cq.select(quote);

        return entityManager.createQuery(cq)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findQuotesOneByOne(EntityManager entityManager) {
        // Native SQL query for quote IDs
        ConnectionFunction<Connection, List<Long>> quoteIdQuery = con -> {
            String sql = String.format("select %s from %s", Quote_.ID, "QUOTE");

            List<Long> ids = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong(1));
                }
            }
            return List.copyOf(ids);
        };
        List<Long> quoteIds = entityManager.callWithConnection(quoteIdQuery);

        // Finding the quotes, one by one, using method EntityManager.find
        // Clearly, this is quite inefficient, and should not be done in practice
        return quoteIds.stream()
                .map(id -> entityManager.find(Quote.class, id))
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static List<Quote> insertQuotes(EntityManager entityManager) {
        QuotesInserter quotesInserter = new QuotesInserter(entityManager);
        return quotesInserter.insertQuotes();
    }
}
