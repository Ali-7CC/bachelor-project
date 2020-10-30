import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sankey {
    private List<String> nodes;
    private HashMap<Relation, Double> links;


    public Sankey(List<String> nodes, HashMap<Relation, Double> links) {
        this.nodes = nodes;
        this.links = links;
    }


    public JSONObject toJSON() {
        // Making the nodes JSONArray
        JSONArray nodesJSONArray = new JSONArray();
        for (String n : this.nodes) {
            JSONObject nodeObj = new JSONObject();
            nodeObj.put("name", n);
            nodesJSONArray.put(nodeObj);
        }


        // Making the links JSONArray
        JSONArray linksJSONArray = new JSONArray();
        for (Map.Entry<Relation, Double> entry : this.links.entrySet()) {
            JSONObject linkObj = new JSONObject();
            linkObj.put("source", entry.getKey().eventNames.get(0));
            linkObj.put("target", entry.getKey().eventNames.get(1));
            linkObj.put("value", entry.getValue());
            linksJSONArray.put(linkObj);
        }

        // Combining nodes and links into one JSONObject
        JSONObject result = new JSONObject();
        result.put("links", linksJSONArray);
        result.put("nodes", nodesJSONArray);
        return result;


    }

}
