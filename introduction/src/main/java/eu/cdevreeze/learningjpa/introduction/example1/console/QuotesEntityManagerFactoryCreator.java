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

import eu.cdevreeze.learningjpa.introduction.example1.entity.Author;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Quote;
import eu.cdevreeze.learningjpa.introduction.example1.entity.Subject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;

/**
 * Factory of EntityManagerFactory objects for the Quotes persistence unit and using a H2 in-memory database.
 *
 * @author Chris de Vreeze
 */
public class QuotesEntityManagerFactoryCreator {

    private QuotesEntityManagerFactoryCreator() {
    }

    public static EntityManagerFactory createEntityManagerFactory() {
        return new PersistenceConfiguration("Quotes")
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .property(PersistenceConfiguration.JDBC_DRIVER, "org.h2.Driver")
                .property(PersistenceConfiguration.JDBC_URL, "jdbc:h2:mem:quotedb") // in-memory
                .property(PersistenceConfiguration.JDBC_USER, "sa")
                .property(PersistenceConfiguration.JDBC_PASSWORD, "")
                .property(PersistenceConfiguration.CACHE_MODE, SharedCacheMode.ENABLE_SELECTIVE) // 2nd level cache by default disabled
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, "drop-and-create") // see Jakarta Persistence spec
                .property("hibernate.show_sql", true) // Hibernate-specific
                .property("hibernate.format_sql", true) // Hibernate-specific
                .property("hibernate.highlight_sql", true) // Hibernate-specific
                .property("hibernate.jpa.compliance.query", true) // Hibernate-specific
                .managedClass(Quote.class)
                .managedClass(Subject.class)
                .managedClass(Author.class)
                .createEntityManagerFactory();
    }
}
