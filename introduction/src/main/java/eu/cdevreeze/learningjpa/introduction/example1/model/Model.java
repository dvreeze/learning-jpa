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

package eu.cdevreeze.learningjpa.introduction.example1.model;

import com.google.common.collect.ImmutableSet;

import java.util.OptionalLong;

/**
 * Immutable quote data model.
 *
 * @author Chris de Vreeze
 */
public class Model {

    private Model() {
    }

    public record Author(OptionalLong idOption, String name) {
    }

    public record Subject(OptionalLong idOption, String subject) {
    }

    public record Quote(OptionalLong idOption, String quoteText, Author attributedTo,
                        ImmutableSet<Subject> subjects) {
    }
}
