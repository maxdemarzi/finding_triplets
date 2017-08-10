package com.maxdemarzi;

import com.maxdemarzi.results.MapResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Combinations {

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    @Procedure(name = "com.maxdemarzi.combinations", mode = Mode.READ)
    @Description("CALL com.maxdemarzi.combinations(tag, number) - find combinations")
    public Stream<MapResult> getCombinations(@Name("tag") String tag, @Name("size") Number k) throws IOException {
        // We start by finding the tag
        Node tagNode = db.findNode(Labels.Tag, "name", tag);

        // We will keep track of the combinations as a String of node ids
        Map<String, Integer> counts = new HashMap<>();

        // Next we find all the documents tagged by this tag
        for (Relationship tagged : tagNode.getRelationships(Direction.INCOMING, RelationshipTypes.TAGGED)) {
            Node testPlan = tagged.getStartNode();

            // Then find all the tags for this test plan
            ArrayList<Long> tags = new ArrayList<>();
            for (Relationship taggedToo : testPlan.getRelationships(Direction.OUTGOING, RelationshipTypes.TAGGED)) {
                tags.add(taggedToo.getEndNodeId());
            }

            // We need to remove our starting tag
            tags.remove(tagNode.getId());

            // Get all the sorted combinations of tags
            List<long[]> list = new ArrayList<>();
            combinations(k.intValue() - 1 , tags.stream().sorted().mapToLong(l -> l).toArray(), list );

            // For each combination add it to the counts map, or increment it
            for (long[] item : list) {
                counts.merge(Arrays.toString(item), 1, (c, one) -> c + 1);
            }
        }

        // Sort the results in descending order
        List<Map.Entry<String,Integer>> results =  new ArrayList<>(counts.entrySet());
        results.sort( Map.Entry.<String, Integer>comparingByValue().reversed() );

        // Get the top 10 results
        results = results.subList(0,Math.min(results.size(), 10));

        // Stream results
        return results.stream().map(result -> {
            Map<String, Object> triple = new HashMap<>();
            int count = 1;
            for (String id : result.getKey().substring(1, result.getKey().length() - 1).replace(" ", "").split(",")) {
                Node found = db.getNodeById(Long.valueOf(id));
                String name = (String)found.getProperty("name");
                triple.put("tag" + count++, name);
            }
            triple.put("count", result.getValue());
            return new MapResult(triple);
        });

    }

    private static void combinations(int k, long[] input, List<long[]> subsets) {

        long[] s = new long[k];
        // pointing to elements in input array

        if (k <= input.length) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < k - 1; i++);
            subsets.add(getSubset(input, s));
            for(;;) {
                int i;
                // find position of item that can be incremented
                for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--);
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < k; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(input, s));
            }
        }
    }

    // generate actual subset by index sequence
    private static long[] getSubset(long[] input, long[] subset) {
        long[] result = new long[subset.length];
        for (int i = 0; i < subset.length; i++)
            result[i] = input[(int)subset[i]];
        return result;
    }

}
