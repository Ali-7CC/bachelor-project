import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChordMatrix {
    double[][] matrix;
    RelationToAggregatedValueMap relations;
    List<String> groups;
    JSONArray json;



    public ChordMatrix(RelationToAggregatedValueMap relations){
        this.relations = relations;
        this.groups = populateGroups(this.relations);
        this.matrix = populateMatrix(this.relations);
        this.json = createJSON();
    }





    private List<String> populateGroups (RelationToAggregatedValueMap relations){
        List<String> distinctGroups = relations.map.keySet().stream().
                flatMap(l -> l.eventNames.stream()).distinct().collect(Collectors.toList());

        return distinctGroups;
    }


    private double[][]  populateMatrix(RelationToAggregatedValueMap relations){
        int numberOfGroups = this.groups.size();
        double[][] matrix = new double[numberOfGroups][numberOfGroups];
        for(Map.Entry<Relation, Double> relation : relations.map.entrySet()){
            String source = relation.getKey().eventNames.get(0);
            int sourceIndex = this.groups.indexOf(source);
            String target =  relation.getKey().eventNames.get(1);
            int targetIndex = this.groups.indexOf(target);
            Double value = relation.getValue();
            matrix[sourceIndex][targetIndex] = value;
        }
        return matrix;
    }


    private JSONArray createJSON(){
        JSONArray result = new JSONArray();
        JSONObject matrix = new JSONObject();
        JSONObject groups = new JSONObject();
        matrix.put("matrix", this.matrix);
        groups.put("groups", this.groups);

        result.put(matrix);
        result.put(groups);
        return result;
    }




/*               Data = [
                            { matrix : [[], [], []]},

                            {groups : []}
                        ]
 */
}
