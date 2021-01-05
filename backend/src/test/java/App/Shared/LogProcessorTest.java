package App.Shared;

import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LogProcessorTest {
    XLog testLog;
    XLog smallLog;
    @Before
    public void setUp() throws Exception {
        File testFile = new File("src/test/java/TestResources/call_center_log.xes");
        XesXmlParser parser = new XesXmlParser();
        testLog = parser.parse(testFile).get(0);
        // Trace 1 (Case 1): Inbound Call -> Handle Case -> Call Outbound
        XTrace trace1 =  testLog.get(0);
        // Trace 2 (Case 17): Inbound Call -> Handle Case -> Handle Case -> Handle Case Inbound Call
        XTrace trace2 =  testLog.get(16);

        // Small log with only 2 traces
        XFactoryBufferedImpl factory = new XFactoryBufferedImpl();
        smallLog = factory.createLog();
        smallLog.getClassifiers().add(testLog.getClassifiers().get(0));
        smallLog.add(trace1);
        smallLog.add(trace2);


    }

    @Test
    public void getValidAttributeKeys() {
        List<String> actual = LogProcessor.getValidAttributeKeys(testLog);
        List<String> expected = new ArrayList<>();
        expected.add("concept:name");
        expected.add("lifecycle:transition");
        expected.add("org:resource");
        expected.add("time:timestamp");
        expected.add("Activity");
        expected.add("Product");
        expected.add("Service_Type");
        expected.add("Resource");

        assertEquals(expected, actual);


    }

    @Test
    public void makeNodeLabel() {
        XEvent event = testLog.get(0).get(0);
        String stringAttrLabel = LogProcessor.makeNodeLabel(event, "Activity", testLog.getClassifiers().get(0));
        assertEquals("Inbound Call", stringAttrLabel);


        String timeLabel = LogProcessor.makeNodeLabel(event, "time:timestamp", testLog.getClassifiers().get(0));
        assertEquals("Inbound Call", timeLabel);




    }

    @Test
    public void getClassifier() {

    }

    @Test
    public void sameTrace() {
    }

    @Test
    public void findVariants() {
    }

    @Test
    public void combineTraces() {
    }

    @Test
    public void combineVariants() {
    }

    @Test
    public void createSubLogs() {
    }

    @Test
    public void relationToValues() {
    }

    @Test
    public void round() {
    }
}