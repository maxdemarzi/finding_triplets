package com.maxdemarzi;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static junit.framework.TestCase.assertEquals;

public class CombinationsTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public final Neo4jRule neo4j = new Neo4jRule()
            .withFixture(MODEL_STATEMENT)
            .withProcedure(Combinations.class);

    @Test
    public void testTriplets() throws Exception {
        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/db/data/transaction/commit").toString(), QUERY1);
        int count = response.get("results").get(0).get("data").size();
        assertEquals(3, count);
        Map results = mapper.convertValue(response.get("results").get(0).get("data").get(0).get("row").get(0), Map.class);
        assertEquals("second tag", results.get("tag1"));
        assertEquals("third tag", results.get("tag2"));
        assertEquals(4, results.get("count"));
    }

    private static final Map QUERY1 =
            singletonMap("statements", singletonList(singletonMap("statement",
                    "CALL com.maxdemarzi.combinations('first tag', 3) yield value return value")));

    @Test
    public void testQuads() throws Exception {
        HTTP.Response response = HTTP.POST(neo4j.httpURI().resolve("/db/data/transaction/commit").toString(), QUERY2);
        int count = response.get("results").get(0).get("data").size();
        assertEquals(1, count);
        Map results = mapper.convertValue(response.get("results").get(0).get("data").get(0).get("row").get(0), Map.class);
        assertEquals("second tag", results.get("tag1"));
        assertEquals("third tag", results.get("tag2"));
        assertEquals("fourth tag", results.get("tag3"));
        assertEquals(3, results.get("count"));
    }


    private static final Map QUERY2 =
            singletonMap("statements", singletonList(singletonMap("statement",
                    "CALL com.maxdemarzi.combinations('first tag', 4) yield value return value")));

    private static final String MODEL_STATEMENT =
            "CREATE (t1:Tag {name:'first tag'})" +
            "CREATE (t2:Tag {name:'second tag'})" +
            "CREATE (t3:Tag {name:'third tag'})" +
            "CREATE (t4:Tag {name:'fourth tag'})" +

            "CREATE (plan1:TestPlan)" +
            "CREATE (plan2:TestPlan)" +
            "CREATE (plan3:TestPlan)" +
            "CREATE (plan4:TestPlan)" +
            "CREATE (plan5:TestPlan)" +
            "CREATE (plan6:TestPlan)" +

            "CREATE (plan1)-[:TAGGED]->(t1)" +
            "CREATE (plan1)-[:TAGGED]->(t2)" +
            "CREATE (plan2)-[:TAGGED]->(t1)" +
            "CREATE (plan2)-[:TAGGED]->(t2)" +
            "CREATE (plan3)-[:TAGGED]->(t1)" +
            "CREATE (plan3)-[:TAGGED]->(t2)" +
            "CREATE (plan3)-[:TAGGED]->(t3)" +
            "CREATE (plan4)-[:TAGGED]->(t1)" +
            "CREATE (plan4)-[:TAGGED]->(t2)" +
            "CREATE (plan4)-[:TAGGED]->(t3)" +
            "CREATE (plan4)-[:TAGGED]->(t4)" +
            "CREATE (plan5)-[:TAGGED]->(t1)" +
            "CREATE (plan5)-[:TAGGED]->(t2)" +
            "CREATE (plan5)-[:TAGGED]->(t3)" +
            "CREATE (plan5)-[:TAGGED]->(t4)" +
            "CREATE (plan6)-[:TAGGED]->(t1)" +
            "CREATE (plan6)-[:TAGGED]->(t2)" +
            "CREATE (plan6)-[:TAGGED]->(t3)" +
            "CREATE (plan6)-[:TAGGED]->(t4)";

}
