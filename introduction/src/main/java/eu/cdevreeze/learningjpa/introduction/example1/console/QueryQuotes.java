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
import eu.cdevreeze.learningjpa.introduction.example1.entity.Subject;
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                .property("jakarta.persistence.jdbc.driver", "org.h2.Driver")
                .property("jakarta.persistence.jdbc.url", "jdbc:h2:mem:quotedb")
                .property("jakarta.persistence.jdbc.user", "sa")
                .property("jakarta.persistence.jdbc.password", "")
                .managedClass(Quote.class)
                .managedClass(Subject.class)
                .managedClass(Author.class)
                .createEntityManagerFactory();
    }

    private static ImmutableList<Model.Quote> findAllQuotes(EntityManager entityManager) {
        return entityManager.createQuery("select qt from Quote qt", Quote.class)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static ImmutableList<Model.Quote> findAllQuotesUsingCriteriaApi(EntityManager entityManager) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Quote> criteriaQuery = criteriaBuilder.createQuery(Quote.class);
        Root<Quote> criteriaRoot = criteriaQuery.from(Quote.class);
        criteriaQuery.select(criteriaRoot);

        return entityManager.createQuery(criteriaQuery)
                .getResultStream()
                .map(Quote::toModel)
                .collect(ImmutableList.toImmutableList());
    }

    private static List<Quote> insertQuotes(EntityManager entityManager) {
        Author wimHof = insertAuthor("Wim Hof", entityManager);
        Author ronPaul = insertAuthor("Ron Paul", entityManager);

        Subject innerStrength = insertSubject("inner strength", entityManager);
        Subject liberty = insertSubject("liberty", entityManager);
        Subject politics = insertSubject("politics", entityManager);
        Subject financialSystem = insertSubject("financial system", entityManager);
        Subject defense = insertSubject("defense", entityManager);
        Subject peace = insertSubject("peace", entityManager);

        List<Quote> quotes = new ArrayList<>();
        quotes.add(insertQuote(
                "If you can learn how to use your mind, anything is possible.",
                wimHof,
                Set.of(innerStrength),
                entityManager
        ));
        quotes.add(insertQuote(
                "I'm not afraid of dying. I'm afraid not to have lived.",
                wimHof,
                Set.of(innerStrength),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        I've come to understand that if you want to learn something badly enough,
                        you'll find a way to make it happen.
                        Having the will to search and succeed is very important""",
                wimHof,
                Set.of(innerStrength),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        In nature, it is not only the physically weak but the mentally weak that get eaten.
                        Now we have created this modern society in which we have every comfort,
                        yet we are losing our ability to regulate our mood, our emotions.""",
                wimHof,
                Set.of(innerStrength),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        Cold is a stressor, so if you are able to get into the cold and control your body's response to it,
                        you will be able to control stress.""",
                wimHof,
                Set.of(innerStrength),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        Justifying conscription to promote the cause of liberty is one of the most bizarre notions ever conceived by man!
                        Forced servitude, with the risk of death and serious injury as a price to live free, makes no sense.""",
                ronPaul,
                Set.of(liberty),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        When the federal government spends more each year than it collects in tax revenues,
                        it has three choices: It can raise taxes, print money, or borrow money.
                        While these actions may benefit politicians, all three options are bad for average Americans.""",
                ronPaul,
                Set.of(liberty),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        Well, I don't think we should go to the moon.
                        I think we maybe should send some politicians up there.""",
                ronPaul,
                Set.of(politics),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        I think a submarine is a very worthwhile weapon.
                        I believe we can defend ourselves with submarines and all our troops back at home.
                        This whole idea that we have to be in 130 countries and 900 bases...
                        is an old-fashioned idea.""",
                ronPaul,
                Set.of(liberty),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        Of course I've already taken a very modest position on the monetary system,
                        I do take the position that we should just end the Fed.""",
                ronPaul,
                Set.of(liberty, financialSystem),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        Legitimate use of violence can only be that which is required in self-defense.""",
                ronPaul,
                Set.of(defense),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        I am absolutely opposed to a national ID card.
                        This is a total contradiction of what a free society is all about.
                        The purpose of government is to protect the secrecy and the privacy of all individuals,
                        not the secrecy of government. We don't need a national ID card.""",
                ronPaul,
                Set.of(liberty),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        Maybe we ought to consider a Golden Rule in foreign policy:
                        Don't do to other nations what we don't want happening to us.
                        We endlessly bomb these countries and then we wonder why they get upset with us?""",
                ronPaul,
                Set.of(liberty, peace),
                entityManager
        ));
        quotes.add(insertQuote(
                """
                        I am just absolutely convinced that the best formula for giving us peace and
                        preserving the American way of life is freedom, limited government,
                        and minding our own business overseas.""",
                ronPaul,
                Set.of(liberty, peace),
                entityManager
        ));
        return List.copyOf(quotes);
    }

    private static Author insertAuthor(String authorName, EntityManager entityManager) {
        Author author = new Author(authorName);
        Preconditions.checkArgument(!entityManager.contains(author));
        entityManager.persist(author);
        Preconditions.checkArgument(entityManager.contains(author));
        return author;
    }

    private static Subject insertSubject(String subject, EntityManager entityManager) {
        Subject subjectEntity = new Subject(subject);
        Preconditions.checkArgument(!entityManager.contains(subjectEntity));
        entityManager.persist(subjectEntity);
        Preconditions.checkArgument(entityManager.contains(subjectEntity));
        return subjectEntity;
    }

    private static Quote insertQuote(String quoteText, Author attributedTo, Set<Subject> subjects, EntityManager entityManager) {
        Quote quote = new Quote(quoteText, attributedTo, subjects);
        Preconditions.checkArgument(!entityManager.contains(quote));
        entityManager.persist(quote);
        Preconditions.checkArgument(entityManager.contains(quote));
        return quote;
    }
}
