import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds the aggregated form of the RelationToValuesMap.
 */
public class RelationToAggregatedValueMap {
    HashMap<Relation, Double> map;
    String attrKey;
    String operator;
    String aggregationFunction;

    public RelationToAggregatedValueMap(HashMap<Relation, Double> map, String attrKey,
                                        String operator, String aggregationFunction) {
        this.map = map;
        this.attrKey = attrKey;
        this.operator = operator;
        this.aggregationFunction = aggregationFunction;
    }

    /**
     * Constructs a JSON of the HashMap in the form {"links" :
     * {"source" : "source attr", "target" : "target attr", "value" : the aggregated value},...
     * }
     *
     * @return The JSONObject with the key "links" and a JSONArray as a value which contains what's described above.
     */
    public JSONObject toJSON() {
        // Making the nodes JSONArray
        List<String> nodesList = this.map.keySet().stream().flatMap(l -> l.eventNames.stream()).distinct().collect(Collectors.toList());
        JSONArray nodes = new JSONArray();
        for(String n : nodesList){
            JSONObject nodeObj = new JSONObject();
            nodeObj.put("name",n);
            nodes.put(nodeObj);
        }
        // Making the links JSONArray
        JSONArray links = new JSONArray();
        for (Map.Entry<Relation, Double> entry : this.map.entrySet()) {
            JSONObject linkObj = new JSONObject();
            linkObj.put("source", entry.getKey().eventNames.get(0));
            linkObj.put("target", entry.getKey().eventNames.get(1));
            linkObj.put("value", entry.getValue());
            links.put(linkObj);
        }
        JSONObject result = new JSONObject();
        result.put("links", links);
        result.put("nodes",nodes);
        return result;
    }

}
