import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;

public class SankeyMainTest {
    public static void main(String[] args) throws Exception {
        // Event log 1 : Disco call center

        // Parsing the log
        String path = System.getProperty("user.dir") + "/src/main/resources/call_center_simple_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        List<XLog> logs = parser.parse(file);
        XLog callCenterLog = logs.get(0);

        // Processing
        LogPreprocessor preprocessor = new LogPreprocessor(callCenterLog);
        RelationToValuesMap relationToValuesMap = new RelationToValuesMap("Activity", "COUNT");
        for(XTrace trace : callCenterLog){
            relationToValuesMap = preprocessor.relationToValues(trace, "Activity", "COUNT",
                    relationToValuesMap);
        }

        // Aggregating
        RelationToAggregatedValueMap aggregatedValueMap = relationToValuesMap.aggregateSum();

        // Printing
        System.out.println(aggregatedValueMap.toJSON());
    }
}
    