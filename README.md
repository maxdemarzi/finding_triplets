# Finding Triplets
Finding triplets using Neo4j

As requested by this Stack Overflow question:
https://stackoverflow.com/questions/45541495/finding-triplets-having-highest-common-relationships-in-neo4j

This project requires Neo4j 3.2.x

Instructions
------------ 

This project uses maven, to build a jar-file with the procedure in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/triplets-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/triplets-1.0-SNAPSHOT.jar neo4j-enterprise-3.2.3/plugins/.


Edit your Neo4j/conf/neo4j.conf file by adding this line:

    dbms.security.procedures.unrestricted=com.maxdemarzi.*    

Restart your Neo4j Server.

Create the Schema by running this stored procedure:

    CALL com.maxdemarzi.schema.generate
    
Create some test data:

    CREATE (t1:Tag {name:'first tag'})
    CREATE (t2:Tag {name:'second tag'})
    CREATE (t3:Tag {name:'third tag'})
    CREATE (t4:Tag {name:'fourth tag'})       
    CREATE (plan1:TestPlan)
    CREATE (plan2:TestPlan)
    CREATE (plan3:TestPlan)
    CREATE (plan4:TestPlan)
    CREATE (plan5:TestPlan)
    CREATE (plan6:TestPlan)       
    CREATE (plan1)-[:TAGGED]->(t1)
    CREATE (plan1)-[:TAGGED]->(t2)
    CREATE (plan2)-[:TAGGED]->(t1)
    CREATE (plan2)-[:TAGGED]->(t2)
    CREATE (plan3)-[:TAGGED]->(t1)
    CREATE (plan3)-[:TAGGED]->(t2)
    CREATE (plan3)-[:TAGGED]->(t3)
    CREATE (plan4)-[:TAGGED]->(t1)
    CREATE (plan4)-[:TAGGED]->(t2)
    CREATE (plan4)-[:TAGGED]->(t3)
    CREATE (plan4)-[:TAGGED]->(t4)
    CREATE (plan5)-[:TAGGED]->(t1)
    CREATE (plan5)-[:TAGGED]->(t2)
    CREATE (plan5)-[:TAGGED]->(t3)
    CREATE (plan5)-[:TAGGED]->(t4)
    CREATE (plan6)-[:TAGGED]->(t1)
    CREATE (plan6)-[:TAGGED]->(t2)
    CREATE (plan6)-[:TAGGED]->(t3)
    CREATE (plan6)-[:TAGGED]->(t4)
    
Call the procedure:
    
    CALL com.maxdemarzi.combinations('first tag', 3);    
    
You should get:

    {
      "tag1": "second tag",
      "count": 4,
      "tag2": "third tag"
    }
    {
      "tag1": "third tag",
      "count": 3,
      "tag2": "fourth tag"
    }
    {
      "tag1": "second tag",
      "count": 3,
      "tag2": "fourth tag"
    }
    
Try a larger number:

    CALL com.maxdemarzi.combinations('first tag', 4);    
