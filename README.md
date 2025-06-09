
# Learning Jakarta Persistence

This project is about deeply learning
[Jakarta Persistence](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)
through its [Hibernate ORM](https://hibernate.org/orm/) implementation.

Jakarta Persistence used to be known as JPA (Java Persistence API), and that term is still used loosely for Jakarta Persistence.

This project contains a lot of both *theory* and *practice*.

The theory is summarized in documents referring to the Jakarta Persistence specification and the Hibernate ORM documentation.
Much of the theory is about making sense of the specification:
* From a "functional programming" perspective, what is the *implicit program state* when using Jakarta Persistence?
* How does this implicit program state conceptually relate to JDBC program state?
* What is the *essence* of Jakarta Persistence, and how can we *think in Jakarta Persistence*? Knowing the essence makes it easier to guess or look up details
* Related to this, what is *the underlying data model*, compared with the relational data model?
* How can we *introspect* Jakarta Persistence "program state", and how can we go down to the JDBC level if needed?

The practice parts are the Maven modules in this Maven project. There are several such Maven modules, exploring variations in:
* Transaction management, distinguishing resource-local entity managers from JTA entity managers
* The level of out-of-the-box JPA and transaction support, ranging from standalone Java SE JPA usage to Spring Boot applications
* Database products, in particular PostgreSQL and MySQL
* Untyped Jakarta Persistence queries versus typed Jakarta Persistence queries

The API documentation of Jakarta Persistence 3.2 is
[Jakarta Persistence 3.2 API](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/module-summary.html).

In [this article](https://www.baeldung.com/jakarta-persistence-3-2) it is shown what version 3.2 of Jakarta Persistence
brings to the table.

The [Short Guide to Hibernate 7](https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html)
can be used as a starting point to use Hibernate ORM. Note that this guide does not limit itself to
the Jakarta Persistence API.

Some useful advice about what to do when things go wrong can also be found in that guide.
See [Hibernate advice](https://docs.jboss.org/hibernate/orm/7.0/introduction/html_single/Hibernate_Introduction.html#advice).

## Creating the project

The initial POM files were generated as described [here](https://www.baeldung.com/maven-multi-module).

