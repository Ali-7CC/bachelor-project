import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Chord {
    private double[][] matrix;
    private List<String> nodes;


    public Chord(double[][] matrix, List<String> nodes ){
        this.matrix = matrix;
        this.nodes = nodes;
    }



    public JSONArray toJSON(){
        JSONArray result = new JSONArray();
        JSONObject matrix = new JSONObject();
        JSONObject nodes = new JSONObject();
        matrix.put("matrix", this.matrix);
        nodes.put("nodes", this.nodes);

        result.put(matrix);
        result.put(nodes);
        return result;
    }
}
