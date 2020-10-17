import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.List;

public class ActivityVariant {
    String ID;
    // Sequence of activities for this variant
    List<XAttribute> sequence;
    // The collection of traces that contain this sequence
    List<XTrace> traces;
    int frequency;


    public ActivityVariant(){
        this.sequence = new ArrayList<>();
        this.traces = new ArrayList<>();
        frequency = 0;
    }

}
