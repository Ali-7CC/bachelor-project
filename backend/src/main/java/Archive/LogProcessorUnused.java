package Archive;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.List;

public class LogProcessorUnused {

    /**
     * Finds all variants in an event log by looking at the sequence of activities in each trace.
     *
     * @param log Event log of type XLog containing one <log> element.
     * @return An object of type Archive.ActivityVariantMap which holds a map of the variants
     */
    public ActivityVariantMap findVariants(XLog log) {
        ActivityVariantMap variants = new ActivityVariantMap();
        for (XTrace trace : log) {
            List<XAttribute> sequence = new ArrayList<>();
            for (XEvent event : trace) {
                XAttribute activity = event.getAttributes().get("Activity");
                sequence.add(activity);
            }
            variants.update(sequence, trace);
        }

        return variants;
    }


}
