import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityVariantMap {
    HashMap<String, ActivityVariant> variants;

    public activityVariantMap(){
        this.variants = new HashMap<>();
    }

    public void update(List<XAttribute> sequence, XTrace trace){
        boolean variantExists = false;
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
