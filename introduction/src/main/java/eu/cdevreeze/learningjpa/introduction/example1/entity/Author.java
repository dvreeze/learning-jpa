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

import eu.cdevreeze.learningjpa.introduction.example1.model.Model;
import jakarta.persistence.*;

import java.util.Optional;

/**
 * Author JPA entity.
 *
 * @author Chris de Vreeze
 */
@Entity
@Table(name = "AUTHOR")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    protected Author() {
    }

    public Author(String name) {
        this.id = null;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model.Author toModel() {
        return new Model.Author(Optional.ofNullable(id).stream().mapToLong(id -> id).findFirst(), name);
    }
}
