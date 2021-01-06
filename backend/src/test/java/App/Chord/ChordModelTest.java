package App.Chord;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ChordModelTest {


    @Test
    public void toJSONString() {
         double[][] matrix = new double[][]{{1.0, 1.0}, {1.0, 1.0}};
         List<String> nodes = Stream.of("A", "B").collect(Collectors.toList());
        ChordModel model = new ChordModel(matrix, nodes);
        String expected = "{\"nodes\":[\"A\",\"B\"],\"matrix\":[[1,1],[1,1]]}";
        String actual = model.toJSONString();
        assertEquals(expected, actual);
    }
}