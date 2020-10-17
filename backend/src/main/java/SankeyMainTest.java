import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SankeyMainTest {
    public static void main(String[] args) throws Exception {
        // Event log 1 : Disco call center

        // Parsing the log
        String path = System.getProperty("user.dir") + "/src/main/resources/call_center_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        List<XLog> logs = parser.parse(file);
        XLog callCenterLog = logs.get(0);



        // Processing
        LogPreprocessor preprocessor = new LogPreprocessor(callCenterLog);
        activityVariantMap variants =  preprocessor.findVariants(callCenterLog);
        RelationToValuesMap RelationToValuesMap = new RelationToValuesMap("Activity", "COUNT");
        for(Map.Entry<String, ActivityVariant> entry : variants.variants.entrySet()){
            RelationToValuesMap = preprocessor.relationToValues(entry.getValue(), "Activity", "COUNT",
                    RelationToValuesMap);
        }

        // Aggregating
        RelationToAggregatedValueMap aggregatedValueMap = RelationToValuesMap.aggregateSum();

        // Printing
        System.out.println(aggregatedValueMap.toJSON());
    }
}
    