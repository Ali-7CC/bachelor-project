import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SankeyMainTest {
    public static void main(String[] args) throws Exception {
        // Parsing the log
        String path = System.getProperty("user.dir") + "/src/main/resources/Hospital_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        List<XLog> logs = parser.parse(file);
        XLog callCenterLog = logs.get(0);
        LogPreprocessor preprocessor = new LogPreprocessor(callCenterLog);


        // Creating the relations map
        RelationToValuesMap relationToValuesMap = new RelationToValuesMap("org:group", "COUNT");
        // Processing
/*
        preprocessor.groups = preprocessor.findTraceGroups(callCenterLog);
*/
        for(XTrace trace : callCenterLog){
            relationToValuesMap = preprocessor.relationToValues(trace, "org:group", "COUNT",
                    true, false, relationToValuesMap);
        }

        // Aggregating
        RelationToAggregatedValueMap aggregatedValueMap = relationToValuesMap.aggregateSum();

        // Printing
        System.out.println(aggregatedValueMap.toJSON());


        // CHORD TEST
        for(Map.Entry<Relation, Double> entry : aggregatedValueMap.map.entrySet()){
            System.out.println(entry.getKey().eventNames + "---> " + entry.getValue() );
        }
        ChordMatrix chordMatrix = new ChordMatrix(aggregatedValueMap);
        System.out.println(chordMatrix.json);


    }
}
    