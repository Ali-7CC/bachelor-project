import Archive.ActivityVariantMap;
import org.deckfour.xes.model.*;

import java.util.ArrayList;
import java.util.List;

public class LogProcessor {




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

    public RelationToValuesMap relationToValues(XTrace trace, String attrKey, String operator, boolean duplicates,
                                                RelationToValuesMap relationToValuesMap) {
        // If trace has 1 event only
        if (trace.size() <= 1) {
            if (!operator.equals("COUNT")) return relationToValuesMap;
            // Creating the link
            XEvent sourceEvent = trace.get(0);
            XAttribute sourceAttribute = sourceEvent.getAttributes().get(attrKey);
            Relation relation = new Relation();
            relation.events.add(sourceEvent);
            relation.events.add(null);

            // Naming the source node based on the given configuration
            String sourceAttributeName = sourceAttribute.toString();
            if (duplicates) {
                relation.eventNames.add(sourceAttributeName + "_0");

            } else {
                relation.eventNames.add(sourceAttribute.toString());
            }

            relation.eventNames.add("End");
            // The value (1.0 is the only valid value)
            double value = 1.0;
            // Adding the relation to the map
            if (relationToValuesMap.map.containsKey(relation)) {
                List<Double> values = relationToValuesMap.map.get(relation);
                values.add(1.0);
                relationToValuesMap.map.put(relation, values);
            } else {
                List<Double> values = new ArrayList<>();
                values.add(1.0);
                relationToValuesMap.map.put(relation, values);
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
                Relation relation = new Relation();
                relation.events.add(sourceEvent);
                relation.events.add(targetEvent);

                // Naming the nodes based on the given configuration
                String sourceAttributeName = sourceAttribute.toString();
                String targetAttributeName = targetAttribute.toString();

                if (duplicates) {
                    relation.eventNames.add(sourceAttribute.toString() + "_" + (i));
                    relation.eventNames.add(targetAttribute.toString() + "_" + (i + 1));
                } else {
                    relation.eventNames.add(sourceAttributeName);
                    relation.eventNames.add(targetAttributeName);
                }


                // Computing the value
                double value = Double.NaN;
                if (operator.equals("SUM")) {
                    value = AttributeOperations.sum(sourceAttribute, targetAttribute);
                } else if (operator.equals("DIFF")) {
                    value = AttributeOperations.diff(sourceAttribute, targetAttribute);
                } else if (operator.equals("COUNT")) {
                    value = 1.0;
                }

                // Adding the relation to the map
                if (relationToValuesMap.map.containsKey(relation)) {
                    List<Double> values = relationToValuesMap.map.get(relation);
                    values.add(value);
                    relationToValuesMap.map.put(relation, values);
                } else {
                    List<Double> values = new ArrayList<>();
                    values.add(value);
                    relationToValuesMap.map.put(relation, values);
                }

                // If its the last relation in the trace, create an extra relation from the last event to the End event
/*                if(i == trace.size() - 2){
                    Relation endRelation = new Relation();
                    endRelation.events.add(targetEvent);
                    endRelation.events.add(null);
                    endRelation.eventNames.add(relation.eventNames.get(1));
                    endRelation.eventNames.add("End");

                    if (relationToValuesMap.map.containsKey(endRelation)) {
                        List<Double> values = relationToValuesMap.map.get(endRelation);
                        values.add(1.0);
                        relationToValuesMap.map.put(endRelation, values);
                    } else {
                        List<Double> values = new ArrayList<>();
                        values.add(1.0);
                        relationToValuesMap.map.put(endRelation, values);
                    }

                }*/


            }

            return relationToValuesMap;


        }

    }


    /**
     * Iterates through a trace and populates the RelationToValuesMap with entries in the form
     * [attribute 1, attribute 2] --> [double 1, double 2,..] based on the chosen attribute and
     * the chosen operation to preform on their values.
     *
     * @param trace               An event log trace of type XTrace
     * @param attrKey             The key for the chosen attribute.
     * @param operator            The operator to apply on attribute' values.
     * @param relationToValuesMap The HashMap that gets populated with the above described entries.
     * @return A RelationToValuesMap HashMap with the new entries extracted from the trace.
     */

}

