package App.Chord;

import App.Shared.LogProcessor;
import App.Shared.Relation;
import App.Shared.RelationToValuesMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds an XLog and a App.Shared.LogProcessor and generates App.Chord.ChordModel(s).
 */
public class ChordGenerator {
    private XLog log;

    public ChordGenerator(XLog log, LogProcessor processor) {
        this.log = log;
    }


    public ChordModel createChord(String attributeKey, String operator, String aggregationFunc) {
        RelationToValuesMap relationsToValues = new RelationToValuesMap(attributeKey, operator);
        for (XTrace trace : this.log) {
            relationsToValues = LogProcessor.relationToValues(trace, attributeKey, operator,
                    false, relationsToValues);
        }

        HashMap<Relation, Double> links = relationsToValues.aggregate(aggregationFunc);

        // Nodes
        List<String> nodes = links.keySet().stream().
                flatMap(l -> l.eventNames.stream()).distinct().collect(Collectors.toList());

        // Matrix
        double[][] matrix = populateMatrix(links, nodes);


        return new ChordModel(matrix, nodes);

    }


    private double[][] populateMatrix(HashMap<Relation, Double> links, List<String> nodes) {
        int numberOfGroups = nodes.size();
        double[][] matrix = new double[numberOfGroups][numberOfGroups];
        for (Map.Entry<Relation, Double> relation : links.entrySet()) {
            String source = relation.getKey().eventNames.get(0);
            int sourceIndex = nodes.indexOf(source);
            String target = relation.getKey().eventNames.get(1);
            int targetIndex = nodes.indexOf(target);
            Double value = relation.getValue();
            matrix[sourceIndex][targetIndex] = value;
        }
        return matrix;
    }

}
