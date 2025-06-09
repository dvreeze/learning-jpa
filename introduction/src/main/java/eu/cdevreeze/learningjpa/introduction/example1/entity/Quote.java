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

package eu.cdevreeze.learningjpa.introduction.example1.entity;

import com.google.common.collect.ImmutableSet;
import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Quote JPA entity.
 *
 * @author Chris de Vreeze
 */
@Entity
@Table(name = "Quote") // This annotation could be left out (i.e. left implicit)
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false) // this implies the column is not nullable when generating the schema
    @Column(length = 5000)
    private String quoteText;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    // implies nullable is false for the JoinColumn if the schema is generated
    @JoinColumn(name = "authorId", foreignKey = @ForeignKey(name = "FkAttributedTo"))
    private Author attributedTo;

    // Alternatively, we could turn the quote-subject mapping into a separate entity
    @ManyToMany
    @JoinTable(
            name = "QuoteSubject",
            joinColumns = @JoinColumn(name = "quoteId", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FkQuoteId")),
            inverseJoinColumns = @JoinColumn(name = "subjectId", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FkSubjectId"))
    )
    // List instead of Set, to avoid having to override equals/hashCode for highly mutable JPA entities
    private List<Subject> subjects;

    protected Quote() {
    }

    public Quote(String quoteText, Author attributedTo, List<Subject> subjects) {
        this.quoteText = quoteText;
        this.attributedTo = attributedTo;
        this.subjects = new ArrayList<>(subjects);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public Author getAttributedTo() {
        return attributedTo;
    }

    public void setAttributedTo(Author attributedTo) {
        this.attributedTo = attributedTo;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    /**
     * Converts this Quote entity to an immutable Quote record in the immutable data model.
     * It is assumed that all associated data is available in this entity. Otherwise, when
     * inside an open EntityManager, lazy fetches would take place, thus increasing the number of SQL queries.
     */
    public Model.Quote toModel() {
        return new Model.Quote(
                Optional.ofNullable(getId()).stream().mapToLong(id -> id).findFirst(),
                getQuoteText(),
                getAttributedTo().toModel(),
                getSubjects().stream().map(Subject::toModel).collect(ImmutableSet.toImmutableSet())
        );
    }
}
