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
        Author isaacNewton = insertAuthor("Isaac Newton");
        Author smedleyButler = insertAuthor("Smedley Butler");
        Author henryKissinger = insertAuthor("Henry Kissinger");
        Author michaelLedeen = insertAuthor("Michael Ledeen");
        Author benRich = insertAuthor("Ben Rich");
        Author nikolaTesla = insertAuthor("Nikola Tesla");

        Subject innerStrength = insertSubject("inner strength");
        Subject liberty = insertSubject("liberty");
        Subject politics = insertSubject("politics");
        Subject financialSystem = insertSubject("financial system");
        Subject defense = insertSubject("defense");
        Subject peace = insertSubject("peace");
        Subject patriotism = insertSubject("patriotism");
        Subject war = insertSubject("war");
        Subject profit = insertSubject("profit");
        Subject genius = insertSubject("genius");
        Subject faith = insertSubject("faith");
        Subject achievements = insertSubject("achievements");
        Subject conquest = insertSubject("conquest");
        Subject racket = insertSubject("racket");
        Subject corruptGovernment = insertSubject("corrupt government");
        Subject hubris = insertSubject("hubris");
        Subject hiddenKnowledge = insertSubject("hidden knowledge");

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
        quotes.add(insertQuote(
                """
                        Real patriotism is a willingness to challenge the government when it's wrong.""",
                ronPaul,
                Set.of(patriotism, liberty)
        ));
        quotes.add(insertQuote(
                """
                        Believe me, the intellectual revolution is going on,
                        and that has to come first before you see the political changes.
                        That's where I'm very optimistic.""",
                ronPaul,
                Set.of(politics)
        ));
        quotes.add(insertQuote(
                """
                        War is never economically beneficial except for those in position to profit from war expenditures.""",
                ronPaul,
                Set.of(war, profit)
        ));
        quotes.add(insertQuote(
                """
                        There is only one kind of freedom and that's individual liberty.
                        Our lives come from our creator and our liberty comes from our creator.
                        It has nothing to do with government granting it.""",
                ronPaul,
                Set.of(liberty)
        ));
        quotes.add(insertQuote(
                "Genius is patience",
                isaacNewton,
                Set.of(genius)
        ));
        quotes.add(insertQuote(
                """
                        Atheism is so senseless.
                        When I look at the solar system,
                        I see the earth at the right distance from the sun to receive the proper amounts of heat and light.
                        This did not happen by chance.""",
                isaacNewton,
                Set.of(faith)
        ));
        quotes.add(insertQuote(
                """
                        If I have seen further than others, it is by standing upon the shoulders of giants.""",
                isaacNewton,
                Set.of(achievements)
        ));
        quotes.add(insertQuote(
                """
                        WAR is a racket.
                        It always has been.
                        It is possibly the oldest, easily the most profitable, surely the most vicious.
                        It is the only one international in scope.
                        It is the only one in which the profits are reckoned in dollars and the losses in lives.""",
                smedleyButler,
                Set.of(war)
        ));
        quotes.add(insertQuote(
                """
                        I spent thirty-three years and four months in active military service as a member of this country's most agile military force,
                        the Marine Corps.
                        I served in all commissioned ranks from Second Lieutenant to Major-General.
                        And during that period, I spent most of my time being a high class muscle-man for Big Business, for Wall Street and for the Bankers.
                        In short, I was a racketeer, a gangster for capitalism.""",
                smedleyButler,
                Set.of(war, conquest, racket)
        ));
        quotes.add(insertQuote(
                """
                        Only those who would be called upon to risk their lives for their country should have the privilege of voting
                        to determine whether the nation should go to war.""",
                smedleyButler,
                Set.of(war)
        ));
        quotes.add(insertQuote(
                """
                        The illegal we do immediately; the unconstitutional takes a little longer.""",
                henryKissinger,
                Set.of(corruptGovernment)
        ));
        quotes.add(insertQuote(
                """
                        Military men are dumb, stupid animals to be used as pawns for foreign policy.""",
                henryKissinger,
                Set.of(corruptGovernment, hubris)
        ));
        quotes.add(insertQuote(
                """
                        Every now and again the United States has to pick up a crappy little country and throw it against a wall
                        just to prove we are serious.""",
                michaelLedeen,
                Set.of(war, hubris)
        ));
        quotes.add(insertQuote(
                "We now have the technology to bring ET home.",
                benRich,
                Set.of(hiddenKnowledge)
        ));
        quotes.add(insertQuote(
                """
                        If you want to find the secrets of the universe, think in terms of energy, frequency and vibration.""",
                nikolaTesla,
                Set.of(hiddenKnowledge)
        ));
        quotes.add(insertQuote(
                """
                        The day science begins to study non-physical phenomena,
                        it will make more progress in one decade than in all the previous centuries of its existence.""",
                nikolaTesla,
                Set.of(hiddenKnowledge)
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
        Quote quote = new Quote(quoteText, attributedTo, new ArrayList<>(subjects));
        Preconditions.checkArgument(!entityManager.contains(quote));
        entityManager.persist(quote);
        Preconditions.checkArgument(entityManager.contains(quote));
        return quote;
    }
}
