import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;

public class SankeyMainTest {
    public static void main(String[] args) throws Exception {
        // Event log 1 : Disco call center

        // Parsing the log
        String path = System.getProperty("user.dir") + "/src/main/resources/call_center_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        List<XLog> logs = parser.parse(file);
        XLog callCenterLog = logs.get(0);
/*        LogPreprocessor preprocessor = new LogPreprocessor(callCenterLog);
        List<List<XTrace>> lists = new ArrayList<>();
        HashMap<Integer, List<List<XTrace>>> groups = new HashMap<>();*/
/*        lists.add(callCenterLog);
        int counter = 0;

        do{
            List<Object> results = preprocessor.findGroups(lists.get(0), groups,counter);
            lists = (List<XTrace>) results.get(0);
            groups = (HashMap<Integer, List<List<XTrace>>>) results.get(1);
            counter++;
        } while(!lists.isEmpty());*/
/*        for(XTrace trace : callCenterLog){
            System.out.println(trace.getAttributes().get("concept:name") + " : " + trace.size());
        }*/
/*        groups = preprocessor.findGroups(callCenterLog);
        for(Map.Entry<Integer, List<List<XTrace>>> entry : groups.entrySet()){
            List<List<XTrace>> group = entry.getValue();
            List<List<XAttribute>> g = group.stream().map(lt -> lt.stream().map(t -> t.getAttributes().get("concept:name")).collect(Collectors.toList())).collect(Collectors.toList());
            System.out.println("MATCHES: " +  entry.getKey() + "  GROUPS: " + g);
        }*/

        // Processing
        LogPreprocessor preprocessor = new LogPreprocessor(callCenterLog);
        TraceGroupsMap groups = preprocessor.groups;
/*        for(Map.Entry<Integer, List<List<XTrace>>> entry : groups.map.entrySet()) {
            List<List<XTrace>> group = entry.getValue();
            List<List<XAttribute>> g = group.stream().map(lt -> lt.stream().map(t -> t.getAttributes().get("concept:name")).collect(Collectors.toList())).collect(Collectors.toList());
            System.out.println("MATCHES: " + entry.getKey() + "  GROUPS: " + g);
        }*/


            RelationToValuesMap RelationToValuesMap = new RelationToValuesMap("Activity", "COUNT");
        for(XTrace trace : callCenterLog){
            RelationToValuesMap = preprocessor.relationToValues(trace ,"Activity", "COUNT",
                    RelationToValuesMap);
        }

        // Aggregating
        RelationToAggregatedValueMap aggregatedValueMap = RelationToValuesMap.aggregateSum();

        // Printing
        System.out.println(aggregatedValueMap.toJSON());
    }
}
    