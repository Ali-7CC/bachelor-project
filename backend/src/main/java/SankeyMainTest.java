import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;

public class SankeyMainTest {
    public static void main(String[] args) throws Exception {
        // Parsing the log
        String path = System.getProperty("user.dir") + "/src/main/resources/call_center_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        List<XLog> logs = parser.parse(file);
        XLog callCenterLog = logs.get(0);
        LogPreprocessor preprocessor = new LogPreprocessor(callCenterLog);


        // Creating the relations map
        RelationToValuesMap relationToValuesMap = new RelationToValuesMap("Activity", "COUNT");
        // Processing
        preprocessor.groups = preprocessor.findTraceGroups(callCenterLog);
        for(XTrace trace : callCenterLog){
            relationToValuesMap = preprocessor.relationToValues(trace, "Activity", "COUNT",
                    true, relationToValuesMap);
        }

        // Aggregating
        RelationToAggregatedValueMap aggregatedValueMap = relationToValuesMap.aggregateSum();

        // Printing
        System.out.println(aggregatedValueMap.toJSON());

    }
}
    