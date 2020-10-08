import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;

public class HelloWorldTest {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/src/main/resources/simple_log.xes";
        File file = new File(path);
        XesXmlParser parser = new XesXmlParser();
        try {
            List<XLog> logs = parser.parse(file);
            XLog log = logs.get(0);
            System.out.println(log.get(0).getAttributes().values());

    } catch(Exception e)

    {
        e.printStackTrace();
    }

}}

