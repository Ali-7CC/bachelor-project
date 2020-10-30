package Archive;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that holds the activity variants for a given log
 */
public class ActivityVariantMap {
    // Variant ID -> Archive.ActivityVariant
    HashMap<String, ActivityVariant> variants;

    public ActivityVariantMap(){
        this.variants = new HashMap<>();
    }

    /**
     * Updates the Archive.ActivityVariantMap with a given trace.
     * If the activity sequence of the given trace matches one of the variants in the map,
     * the trace is added to that variant. If not, a new variant is created and the trace is
     * added to it.
     * @param sequence
     * @param trace
     */
    public void update(List<XAttribute> sequence, XTrace trace){
        boolean variantExists = false;
        // Iterates over the variants in the map and adds the new trace to the matching variant.
        for(Map.Entry<String, ActivityVariant> entry : this.variants.entrySet()){
            if(entry.getValue().sequence.equals(sequence)){
                variantExists = true;
                ActivityVariant variant = entry.getValue();
                variant.traces.add(trace);
                variant.frequency++;
                this.variants.put(variant.ID, variant);
                break;
            }
        } if(!variantExists){
            // If no variant matches the given trace, a new variant is created.
            ActivityVariant variant = new ActivityVariant();
            String ID = "V" + this.variants.size();
            variant.ID = ID;
            variant.sequence = sequence;
            variant.traces.add(trace);
            variant.frequency++;
            this.variants.put(ID, variant);
        }

    }

}
