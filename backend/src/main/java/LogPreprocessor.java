import org.deckfour.xes.model.*;

import java.util.ArrayList;
import java.util.List;

public class LogPreprocessor {
    XLog log;

    public LogPreprocessor(XLog log) {
        this.log = log;
    }

    /**
     * Iterates through a trace and populates the relationToValuesMap with entries in the form
     * [attribute 1, attribute 2] --> [double 1, double 2,..] based on the chosen attribute and
     * the chosen operation to preform on their values.
     *
     * @param trace               An event log trace of type XTrace
     * @param attrKey             The key for the chosen attribute.
     * @param operator            The operator to apply on attribute' values.
     * @param relationToValuesMap The HashMap that gets populated with the above described entries.
     * @return A RelationToValuesMap HashMap with the new entries extracted from the trace.
     */
    public RelationToValuesMap relationToValues(XTrace trace, String attrKey, String operator,
                                                RelationToValuesMap relationToValuesMap) {
        // If trace has 1 event only
        if (trace.size() <= 1) {
            if (!operator.equals("COUNT")) return relationToValuesMap;
            // Creating the link
            XEvent event = trace.get(0);
            XAttribute eventAttr = event.getAttributes().get(attrKey);
            List<String> link = new ArrayList<String>();
            link.add(eventAttr.toString() + "_0");
            link.add("$");
            // The value (1.0 is the only valid value)
            double value = 1.0;
            // Adding the relation to the map
            if (relationToValuesMap.map.containsKey(link)) {
                List<Double> values = relationToValuesMap.map.get(link);
                values.add(1.0);
                relationToValuesMap.map.put(link, values);
            } else {
                List<Double> values = new ArrayList<>();
                values.add(1.0);
                relationToValuesMap.map.put(link, values);
            }

            return relationToValuesMap;
        }

        // If the trace has 2 or more events
        else {
            // Iterating over events
            for (int i = 0; i < trace.size() - 1; i++) {
                XEvent sourceEvent = trace.get(i);
                XEvent targetEvent = trace.get(i + 1);
                XAttribute sourceAttribute = sourceEvent.getAttributes().get(attrKey);
                XAttribute targetAttribute = targetEvent.getAttributes().get(attrKey);

                // Creating the link using activity name
                List<String> link = new ArrayList<>();
                link.add(sourceAttribute.toString() + "_" + i);
                link.add(targetAttribute.toString() + "_" + (i + 1));

                // Computing the value
                double value = Double.NaN;
                if (operator.equals("SUM")) {
                    value = AttributeOperations.sum(sourceAttribute, targetAttribute);
                } else if (operator.equals("COUNT")) {
                    value = 1.0;
                }

                // Adding the relation to the map
                if (relationToValuesMap.map.containsKey(link)) {
                    List<Double> values = relationToValuesMap.map.get(link);
                    values.add(value);
                    relationToValuesMap.map.put(link, values);
                } else {
                    List<Double> values = new ArrayList<>();
                    values.add(value);
                    relationToValuesMap.map.put(link, values);
                }
            }

            return relationToValuesMap;


        }

    }
}

