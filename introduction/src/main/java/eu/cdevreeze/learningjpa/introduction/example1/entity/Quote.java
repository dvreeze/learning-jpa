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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Quote JPA entity.
 *
 * @author Chris de Vreeze
 */
@Entity
@Table(name = "QUOTE")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false) // this implies the column is not nullable
    @Column(length = 5000)
    private String quoteText;

    @ManyToOne(optional = false) // implies nullable is false for the JoinColumn
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey())
    private Author attributedTo;

    @ManyToMany
    @JoinTable(
            name = "QUOTE_SUBJECT",
            joinColumns = @JoinColumn(name = "quote_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id")
    )
    private Set<Subject> subjects;

    protected Quote() {
    }

    public Quote(String quoteText, Author attributedTo, Set<Subject> subjects) {
        this.quoteText = quoteText;
        this.attributedTo = attributedTo;
        this.subjects = new HashSet<>(subjects);
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

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Model.Quote toModel() {
        return new Model.Quote(
                Optional.ofNullable(getId()).stream().mapToLong(id -> id).findFirst(),
                getQuoteText(),
                getAttributedTo().toModel(),
                getSubjects().stream().map(Subject::toModel).collect(ImmutableSet.toImmutableSet())
        );
    }
}
