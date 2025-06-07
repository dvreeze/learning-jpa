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

/**
 * Deeply immutable data model, using immutable Java records and immutable Guava collections.
 * Unlike JPA entities, these model objects are not only immutable, but they also have no lazy versus
 * eager loading issues, and they have trivial and stable (generated) equals and hashCode methods.
 *
 * @author Chris de Vreeze
 */
package eu.cdevreeze.learningjpa.introduction.example1.model;
