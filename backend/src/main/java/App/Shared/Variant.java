package App.Shared;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.List;

public class Variant {
    String ID;
    public List<XTrace> traces;
    double frequency;
    public double percentage;

    public Variant(){
        this.traces = new ArrayList<>();
        frequency = 0.0;
        percentage = 0.0;
    }

    public double getPercentage(){
        return this.percentage;
    }

}
