# POLYGOT
Polygot is a Database access and FRM (Functional Relational Mapping) tool which aims
to simplify handling database operations.

This project is heavily inspired by [Slick from Lightbend](https://scala-slick.org/).

**CURRENT VERSION : 0.1.0** 

## ROADMAP
### VERSION : 1.0.0
This build [or] release will aim at connecting with different data sources using JDBC connections with 
unified interface and retrieve results as unified resultset. This build will also be focused on generating
scala code associated with schema / table / columns present in ddatabase (datastore). 
- [x] Connect to databases using JDBC connections.
    - [x] unified interface for all different types of JDBC Connections (keeping it generic).
    - [x] unified result format for all different types of JDBC Connections.
- [x] Tracking all metrics (initialize, compile, execute, retrieve), helps analyze query performance. 
- [ ] Scala codegen
    - [x] Ability to get catalog information (with all metadata information)
    - [x] Ability to get table information (with all metadata information)
    - [x] Ability to get column information (with all metadata information)
    - [ ] Generate object with schema name.
    - [ ] Generate case classes inside schema object.
        - [ ] table name will be the case class name.
        - [ ] case class fields should be based on column name and type.

### VERSION : 2.X.X
This build [or] release will aim at using the generated scala code and use it with the polygot connectors.
It'll be similar to how slick interacts with database using lifted queries and object references.

There's still a lot to think from architecture POV for this build, hence there's no fixed roadmap yet.