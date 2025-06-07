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
 * Jakarta Persistence (JPA) entities. These entities are mutable Java objects representing
 * relational database data. JPA entities should not be used as (immutable) model data or data transfer objects.
 * For that, immutable Java records are much better candidates.
 * <p>
 * The entities in the persistence unit have a reason to exist on their own. Hence, there is hardly
 * any "cascading behaviour" configured in the entities. The JPQL selection queries do need "fetch joins"
 * in order to get complete object graphs for the returned Quote entities, including the author and subjects
 * as parts of those object graphs.
 *
 * @author Chris de Vreeze
 */
package eu.cdevreeze.learningjpa.introduction.example1.entity;
