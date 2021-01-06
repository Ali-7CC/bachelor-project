package App.Sankey;

import App.Shared.Relation;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SankeyModelTest {

    @Test
    public void toJSONString() {
        List<String> nodes = Stream.of("A", "B").collect(Collectors.toList());
        Relation relation = new Relation();
        relation.eventNames = nodes;
        HashMap<Relation, Double> links = new HashMap<>();
        links.put(relation, 1.0);
        SankeyModel model = new SankeyModel(nodes, links);
        String expected = "{\"nodes\":[{\"name\":\"A\"},{\"name\":\"B\"}],\"links\":[{\"source\":\"A\",\"value\":1,\"target\":\"B\"}]}";

        String actual = model.toJSONString();
        assertEquals(expected, actual);
    }
}