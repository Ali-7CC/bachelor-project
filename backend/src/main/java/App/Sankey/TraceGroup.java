package App.Sankey;

import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.List;

/**
 * A trace group represents a collection of traces that share the same first n activities.
 * This class hold the list of such traces and the index to which their activities match.
 */
public class TraceGroup {
    List<XTrace> traces;
    int index;

    public TraceGroup() {
        this.traces = new ArrayList<>();
        this.index = -1;
    }
}


