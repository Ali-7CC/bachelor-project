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

        // Sankey
        SankeyGenerator sankeyGenerator = new SankeyGenerator(callCenterLog, processor);
        Sankey activityCountSankey = sankeyGenerator.createSankey("Activity", "COUNT",
                "SUM", true);
        System.out.println(activityCountSankey.toJSON());


        // Chord
/*        ChordGenerator chordGenerator = new ChordGenerator(callCenterLog, processor);
        Chord activityCountChord = chordGenerator.createChord("Activity", "COUNT", "SUM");
        System.out.println(activityCountChord.toJSON());*/





        // Hospital log
  /*      String hospitalPath = System.getProperty("user.dir") + "/src/main/resources/Hospital_log.xes";
        File hospitalFile = new File(hospitalPath);
        List<XLog> hospitalLogs = parser.parse(hospitalFile);
        XLog hospitalLog = hospitalLogs.get(0);

        // Sankey
*//*        SankeyGenerator hospitalSankeyGenerator = new SankeyGenerator(hospitalLog, processor);
        Sankey orgCountSankey = sankeyGenerator.createSankey("org:group", "COUNT",
                "SUM", true);

        System.out.println(activityCountSankey.toJSON());*//*


        // Chord
        ChordGenerator hospitalChordGenerator = new ChordGenerator(hospitalLog, processor);
        Chord orgCountChord = hospitalChordGenerator.createChord("org:group", "COUNT", "SUM");
        System.out.println(orgCountChord.toJSON());*/


        // Eye tracking log

        // Parsing the log
        String eyePath = System.getProperty("user.dir") + "/src/main/resources/3AOIs.xes";
        File eyeFile = new File(eyePath);
        List<XLog> eyeLogs = parser.parse(eyeFile);
        XLog eyeTrackingLog = eyeLogs.get(0);

        // Sankey
/*
        SankeyGenerator sankeyGenerator3 = new SankeyGenerator(eyeTrackingLog, processor);
        Sankey activityCountSankey3 = sankeyGenerator3.createSankey("Activity", "COUNT",
                "SUM", true);
        System.out.println(activityCountSankey3.toJSON());


        // Chord
        ChordGenerator chordGenerator3 = new ChordGenerator(eyeTrackingLog, processor);
        Chord activityCountChord3 = chordGenerator3.createChord("Activity", "COUNT", "SUM");
        System.out.println(activityCountChord3.toJSON());
*/


    }











}
    