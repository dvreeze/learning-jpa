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
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

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
            emf.getSchemaManager().create(true);
            emf.getSchemaManager().validate();

            ImmutableList<Model.Quote> insertedQuotes =
                    emf.callInTransaction(em ->
                            insertQuotes(em).stream().map(Quote::toModel).collect(ImmutableList.toImmutableList()));

            ImmutableList<Model.Quote> queriedQuotes =
                    emf.callInTransaction(QueryQuotes::findAllQuotes);

            Preconditions.checkArgument(queriedQuotes.equals(insertedQuotes));

            // The same query, as criteria API query, depending on the generated meta model
            ImmutableList<Model.Quote> queriedQuotesUsingCriteriaApi =
                    emf.callInTransaction(QueryQuotes::findAllQuotesUsingCriteriaApi);

            Preconditions.checkArgument(queriedQuotesUsingCriteriaApi.equals(insertedQuotes));

            queriedQuotes.forEach(qt -> {
                System.out.println();
                System.out.println(qt);
            });

            // Not dropping the schema
        } catch (SchemaValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        return new PersistenceConfiguration("Quotes")
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .property(PersistenceConfiguration.JDBC_DRIVER, "org.h2.Driver")
                .property(PersistenceConfiguration.JDBC_URL, "jdbc:h2:mem:quotedb")
                .property(PersistenceConfiguration.JDBC_USER, "sa")
                .property(PersistenceConfiguration.JDBC_PASSWORD, "")
                .property(PersistenceConfiguration.CACHE_MODE, SharedCacheMode.ENABLE_SELECTIVE) // 2nd level cache by default disabled
                .property("hibernate.show_sql", true) // Hibernate-specific
                .property("hibernate.format_sql", true) // Hibernate-specific
                .property("hibernate.highlight_sql", true) // Hibernate-specific
                .managedClass(Quote.class)
                .managedClass(Subject.class)
                .managedClass(Author.class)
                .createEntityManagerFactory();
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

    private static List<Quote> insertQuotes(EntityManager entityManager) {
        QuotesInserter quotesInserter = new QuotesInserter(entityManager);
        return quotesInserter.insertQuotes();
    }
}
