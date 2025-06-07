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
import eu.cdevreeze.learningjpa.introduction.example1.entity.Author;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Subject;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Sample quotes inserter using JPA.
 *
 * @author Chris de Vreeze
 */
public class QuotesInserter {

    private final EntityManager entityManager;

    public QuotesInserter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<Quote> insertQuotes() {
        Author wimHof = insertAuthor("Wim Hof");
        Author ronPaul = insertAuthor("Ron Paul");

        Subject innerStrength = insertSubject("inner strength");
        Subject liberty = insertSubject("liberty");
        Subject politics = insertSubject("politics");
        Subject financialSystem = insertSubject("financial system");
        Subject defense = insertSubject("defense");
        Subject peace = insertSubject("peace");

        List<Quote> quotes = new ArrayList<>();
        quotes.add(insertQuote(
                "If you can learn how to use your mind, anything is possible.",
                wimHof,
                Set.of(innerStrength)
        ));
        quotes.add(insertQuote(
                "I'm not afraid of dying. I'm afraid not to have lived.",
                wimHof,
                Set.of(innerStrength)
        ));
        quotes.add(insertQuote(
                """
                        I've come to understand that if you want to learn something badly enough,
                        you'll find a way to make it happen.
                        Having the will to search and succeed is very important""",
                wimHof,
                Set.of(innerStrength)
        ));
        quotes.add(insertQuote(
                """
                        In nature, it is not only the physically weak but the mentally weak that get eaten.
                        Now we have created this modern society in which we have every comfort,
                        yet we are losing our ability to regulate our mood, our emotions.""",
                wimHof,
                Set.of(innerStrength)
        ));
        quotes.add(insertQuote(
                """
                        Cold is a stressor, so if you are able to get into the cold and control your body's response to it,
                        you will be able to control stress.""",
                wimHof,
                Set.of(innerStrength)
        ));
        quotes.add(insertQuote(
                """
                        Justifying conscription to promote the cause of liberty is one of the most bizarre notions ever conceived by man!
                        Forced servitude, with the risk of death and serious injury as a price to live free, makes no sense.""",
                ronPaul,
                Set.of(liberty)
        ));
        quotes.add(insertQuote(
                """
                        When the federal government spends more each year than it collects in tax revenues,
                        it has three choices: It can raise taxes, print money, or borrow money.
                        While these actions may benefit politicians, all three options are bad for average Americans.""",
                ronPaul,
                Set.of(liberty)
        ));
        quotes.add(insertQuote(
                """
                        Well, I don't think we should go to the moon.
                        I think we maybe should send some politicians up there.""",
                ronPaul,
                Set.of(politics)
        ));
        quotes.add(insertQuote(
                """
                        I think a submarine is a very worthwhile weapon.
                        I believe we can defend ourselves with submarines and all our troops back at home.
                        This whole idea that we have to be in 130 countries and 900 bases...
                        is an old-fashioned idea.""",
                ronPaul,
                Set.of(liberty)
        ));
        quotes.add(insertQuote(
                """
                        Of course I've already taken a very modest position on the monetary system,
                        I do take the position that we should just end the Fed.""",
                ronPaul,
                Set.of(liberty, financialSystem)
        ));
        quotes.add(insertQuote(
                """
                        Legitimate use of violence can only be that which is required in self-defense.""",
                ronPaul,
                Set.of(defense)
        ));
        quotes.add(insertQuote(
                """
                        I am absolutely opposed to a national ID card.
                        This is a total contradiction of what a free society is all about.
                        The purpose of government is to protect the secrecy and the privacy of all individuals,
                        not the secrecy of government. We don't need a national ID card.""",
                ronPaul,
                Set.of(liberty)
        ));
        quotes.add(insertQuote(
                """
                        Maybe we ought to consider a Golden Rule in foreign policy:
                        Don't do to other nations what we don't want happening to us.
                        We endlessly bomb these countries and then we wonder why they get upset with us?""",
                ronPaul,
                Set.of(liberty, peace)
        ));
        quotes.add(insertQuote(
                """
                        I am just absolutely convinced that the best formula for giving us peace and
                        preserving the American way of life is freedom, limited government,
                        and minding our own business overseas.""",
                ronPaul,
                Set.of(liberty, peace)
        ));
        return List.copyOf(quotes);
    }

    private Author insertAuthor(String authorName) {
        Author author = new Author(authorName);
        Preconditions.checkArgument(!entityManager.contains(author));
        entityManager.persist(author);
        Preconditions.checkArgument(entityManager.contains(author));
        return author;
    }

    private Subject insertSubject(String subject) {
        Subject subjectEntity = new Subject(subject);
        Preconditions.checkArgument(!entityManager.contains(subjectEntity));
        entityManager.persist(subjectEntity);
        Preconditions.checkArgument(entityManager.contains(subjectEntity));
        return subjectEntity;
    }

    private Quote insertQuote(String quoteText, Author attributedTo, Set<Subject> subjects) {
        Quote quote = new Quote(quoteText, attributedTo, subjects);
        Preconditions.checkArgument(!entityManager.contains(quote));
        entityManager.persist(quote);
        Preconditions.checkArgument(entityManager.contains(quote));
        return quote;
    }
}
