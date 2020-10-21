import org.deckfour.xes.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LogPreprocessor {
    XLog log;
    TraceGroupsMap groups;

    public LogPreprocessor(XLog log) {
        this.log = log;
    }


    /**
     * Finds the TraceGroups for a given event log and returns a TraceGroupsMap with the found trace groups.
     *
     * @param log An event log of type XLog or List<XTrace>
     * @return A TraceGroupMap that maps the index to which the traces share their first activities to
     * the list of TraceGroup
     */
    public TraceGroupsMap findTraceGroups(List<XTrace> log) {
        HashMap<Integer, List<TraceGroup>> groupsMap = new HashMap<>();
        List<TraceGroup> groupsMaster = new ArrayList<>();
        TraceGroup initialGroup = new TraceGroup();
        initialGroup.traces = log;
        groupsMaster.add(initialGroup);
        int position = 0;
        while (!groupsMaster.isEmpty()) {
            List<TraceGroup> newMaster = new ArrayList<>();
            for (TraceGroup group : groupsMaster) {
                List<TraceGroup> newGroups = groupBynthActivity(group, position);
                for (TraceGroup newGroup : newGroups) {
                    if (newGroup.traces.size() > 1) {
                        // Updating the groupsMap with the new groups
                        if (groupsMap.containsKey(position)) {
                            List<TraceGroup> traceGroups = groupsMap.get(position);
                            traceGroups.add(newGroup);
                            groupsMap.put(position, traceGroups);
                        } else {
                            List<TraceGroup> newTraceGroups = new ArrayList<>();
                            newTraceGroups.add(newGroup);
                            groupsMap.put(position, newTraceGroups);
                        }
                        // Adding the new groups to the newMaster so they can be divided further
                        newMaster.add(newGroup);
                    }
                }
            }
            groupsMaster = newMaster;
            position++;
        }
        TraceGroupsMap traceGroupsMap = new TraceGroupsMap();
        traceGroupsMap.map = groupsMap;
        return traceGroupsMap;
    }


    /**
     * Divides a given TraceGroup into more refined trace groups.
     * i.e. if the given TraceGroup shares activities up to index 3,
     * The returned list of groups that contain >=2 traces share activities up to index 4.
     *
     * @param group The TraceGroup to be divided/refined
     * @param n     The index that the grouping is based on, which equals the current index
     *              of the given TraceGroup + 1
     * @return A list of TraceGroup. Each TraceGroup in that list that contains 2 or more traces share
     * the first n activities.
     */
    public List<TraceGroup> groupBynthActivity(TraceGroup group, int n) {
        // Initializing the list to be returned
        List<TraceGroup> traceGroups = new ArrayList<>();
        // Traces that are shorter or equal to n are immediately made into a new TraceGroup and added to the
        // list to be returned. Longer traces are kept for the grouping.
        TraceGroup longGroups = new TraceGroup();
        for (XTrace trace : group.traces) {
            if (trace.size() <= n) {
                TraceGroup shortGroup = new TraceGroup();
                shortGroup.traces.add(trace);
                traceGroups.add(shortGroup);
            } else {
                longGroups.traces.add(trace);
            }
        }
        // Grouping the longer traces
        List<List<XTrace>> groups = longGroups.traces.stream().collect(Collectors
                .groupingBy(trace -> trace.get(n).getAttributes().get("Activity")))
                .values().stream().collect(Collectors.toList());

        // Constructing the new trace groups that share their activities up to index n.
        for (List<XTrace> newGroup : groups) {
            TraceGroup newTraceGroup = new TraceGroup();
            newTraceGroup.traces = newGroup;
            newTraceGroup.index = n;
            traceGroups.add(newTraceGroup);
        }

        return traceGroups;
    }


    /**
     * Finds all variants in an event log by looking at the sequence of activities in each trace.
     *
     * @param log Event log of type XLog containing one <log> element.
     * @return An object of type ActivityVariantMap which holds a map of the variants
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
    public RelationToValuesMap relationToValues(XTrace trace, String attrKey, String operator, boolean grouping,
                                                RelationToValuesMap relationToValuesMap) {
        // If trace has 1 event only
        if (trace.size() <= 1) {
            if (!operator.equals("COUNT")) return relationToValuesMap;
            // Creating the link
            XEvent event = trace.get(0);
            XAttribute eventAttr = event.getAttributes().get(attrKey);
            Relation relation = new Relation();
            relation.events.add(event);
            relation.events.add(null);
            if(grouping){
                String groupName = this.groups.makeGroupName(trace, 0);
                relation.eventNames.add(eventAttr.toString() + "_0_" + "(" + groupName + ")");
            } else{
                relation.eventNames.add(eventAttr.toString() + "_0");
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
                if(grouping){
                    String sourceGroupName = this.groups.makeGroupName(trace, i);
                    String targetGroupName = this.groups.makeGroupName(trace, i + 1);
                    relation.eventNames.add(sourceAttribute.toString() + "_" + (i) + "_" + "(" + sourceGroupName + ")");
                    relation.eventNames.add(targetAttribute.toString() + "_" + (i + 1) + "_" + "(" + targetGroupName + ")");
                } else{
                    relation.eventNames.add(sourceAttribute.toString() + "_" + (i));
                    relation.eventNames.add(targetAttribute.toString() + "_" + (i + 1));
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
}

