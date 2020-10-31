import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {

        // Log processor
        LogProcessor processor = new LogProcessor();

        // Call Center Log

        // Parsing the log
        String path = System.getProperty("user.dir") + "/src/main/resources/call_center_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        List<XLog> logs = parser.parse(file);
        XLog callCenterLog = logs.get(0);

        // SankeyModel
        SankeyGenerator sankeyGenerator = new SankeyGenerator(callCenterLog, processor);
        SankeyModel activityCountSankeyModel = sankeyGenerator.createSankey("Activity", "COUNT",
                "SUM", true);
        System.out.println(activityCountSankeyModel.toJSON());


        // ChordModel
/*        ChordGenerator chordGenerator = new ChordGenerator(callCenterLog, processor);
        ChordModel activityCountChord = chordGenerator.createChord("Activity", "COUNT", "SUM");
        System.out.println(activityCountChord.toJSON());*/





        // Hospital log
  /*      String hospitalPath = System.getProperty("user.dir") + "/src/main/resources/Hospital_log.xes";
        File hospitalFile = new File(hospitalPath);
        List<XLog> hospitalLogs = parser.parse(hospitalFile);
        XLog hospitalLog = hospitalLogs.get(0);

        // SankeyModel
*//*        SankeyGenerator hospitalSankeyGenerator = new SankeyGenerator(hospitalLog, processor);
        SankeyModel orgCountSankey = sankeyGenerator.createSankey("org:group", "COUNT",
                "SUM", true);

        System.out.println(activityCountSankeyModel.toJSON());*//*


        // ChordModel
        ChordGenerator hospitalChordGenerator = new ChordGenerator(hospitalLog, processor);
        ChordModel orgCountChord = hospitalChordGenerator.createChord("org:group", "COUNT", "SUM");
        System.out.println(orgCountChord.toJSON());*/


        // Eye tracking log

        // Parsing the log
        String eyePath = System.getProperty("user.dir") + "/src/main/resources/3AOIs.xes";
        File eyeFile = new File(eyePath);
        List<XLog> eyeLogs = parser.parse(eyeFile);
        XLog eyeTrackingLog = eyeLogs.get(0);

        // SankeyModel
/*
        SankeyGenerator sankeyGenerator3 = new SankeyGenerator(eyeTrackingLog, processor);
        SankeyModel activityCountSankey3 = sankeyGenerator3.createSankey("Activity", "COUNT",
                "SUM", true);
        System.out.println(activityCountSankey3.toJSON());


        // ChordModel
        ChordGenerator chordGenerator3 = new ChordGenerator(eyeTrackingLog, processor);
        ChordModel activityCountChord3 = chordGenerator3.createChord("Activity", "COUNT", "SUM");
        System.out.println(activityCountChord3.toJSON());
*/


    }











}
    