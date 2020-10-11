import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the aggregated form of the RelationToValuesMap.
 */
public class RelationToAggregatedValueMap {
    HashMap<List<String>, Double> map;
    String attrKey;
    String operator;
    String aggregationFunction;

    public RelationToAggregatedValueMap(HashMap<List<String>, Double> map, String attrKey,
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
        JSONArray links = new JSONArray();
        for (Map.Entry<List<String>, Double> entry : this.map.entrySet()) {
            JSONObject obj = new JSONObject();
            obj.put("source", entry.getKey().get(0));
            obj.put("target", entry.getKey().get(1));
            obj.put("value", entry.getValue());
            links.put(obj);
        }
        JSONObject result = new JSONObject();
        result.put("links", links);
        return result;
    }

}
