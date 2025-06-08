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
 * The entities in this quotes persistence unit have a reason to exist on their own. Hence, there is hardly
 * any "cascading behaviour" configured in the entities.
 * <p>
 * The entities have no overridden equals/hashCode methods. After all, they are highly mutable, so equality
 * would not be stable value equality. In Hibernate documentation the advice is given to override equals/hashCode,
 * however. There it is said that entity equality should be based on a (stable) "natural key" of the entity.
 * Hibernate has a @NaturalId annotation to make the natural key fields explicit, but JPA does not have this annotation.
 * Also note that equality based on a natural key of the entity is not really value equality. So, still I am
 * not a fan of overriding equals/hashCode for entities, but the consequence is that entities should then have
 * no Set-based associations.
 * <p>
 * Each JPA entity has an explicit or implicit {@link jakarta.persistence.Table} annotation (for the "primary table").
 * Other JPA annotations on the entity are either at the "SQL level" or at the higher object-oriented abstraction level.
 * See <a href="https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#basic-attributes">basic attributes</a>,
 * where a distinction is made between annotations concerning the "(O/R) mapping layer" and annotations concerning the "logical layer".
 * The remarks above show that each entity instance is roughly a mutable representation as Java object of a <em>database table row</em>,
 * enhanced with "logical information" about relationships to other tables, etc. This also shows what
 * JPQL is about. JPQL is essentially "SQL, enhanced with language constructs concerning the logical layer".
 * Put differently, JPQL is an object-oriented SQL dialect. The translation of JPQL to SQL is about mapping the
 * OO "logical layer" language constructs to plain SQL.
 *
 * @author Chris de Vreeze
 */
package eu.cdevreeze.learningjpa.introduction.example1.entity;
