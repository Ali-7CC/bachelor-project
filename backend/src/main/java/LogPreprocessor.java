import org.deckfour.xes.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogPreprocessor {
    XLog log;
    TraceGroups groups;

    public LogPreprocessor(XLog log) {
        this.log = log;
        this.groups = findGroups(this.log);
    }


    public TraceGroups findGroups(List<XTrace> log) {
        HashMap<Integer, List<List<XTrace>>> groupsMap = new HashMap<>();
        List<List<XTrace>> master = new ArrayList<>();
        master.add(log);
        int position = 0;
        while (!master.isEmpty()) {
            List<List<XTrace>> newMaster = new ArrayList<>();
            for (List<XTrace> traces : master) {
                List<List<XTrace>> newGroups = groupBynthActivity(traces, position);
                for (List<XTrace> newGroup : newGroups) {
                    if (newGroup.size() > 1) {
                        if (groupsMap.containsKey(position)) {
                            List<List<XTrace>> traceGroup = groupsMap.get(position);
                            traceGroup.add(newGroup);
                            groupsMap.put(position, traceGroup);
                        } else {
                            List<List<XTrace>> newTraceGroup = new ArrayList<>();
                            newTraceGroup.add(newGroup);
                            groupsMap.put(position, newTraceGroup);
                        }
                        newMaster.add(newGroup);

                    }


                }


            }
            master = newMaster;
            position++;
        }
        TraceGroups traceGroups = new TraceGroups();
        traceGroups.map = groupsMap;
        return traceGroups;


    }




 /*   public List<Object> findGroups(List<XTrace> lists, HashMap<Integer, List<List<XTrace>>> groupsMap,
                                                          int position){
        List<List<XTrace>> newGroups = groupBynthActivity(lists,position);
        List<List<XTrace>> toBeRemoved = new ArrayList<>();
        for(List<XTrace> newGroup : newGroups){
            // If the new group is a single
            if(newGroup.size() <= 1){
                if (groupsMap.containsKey(position)) {
                    List<List<XTrace>> traceGroup = groupsMap.get(position);
                    traceGroup.add(newGroup);
                    groupsMap.put(position, traceGroup);
                    toBeRemoved.add(newGroup);
                } else {
                    List<List<XTrace>> newTraceGroup = new ArrayList<>();
                    newTraceGroup.add(newGroup);
                    groupsMap.put(position, newTraceGroup);
                }

            }
        }
        for(List<XTrace> traceList : toBeRemoved){
            newGroups.remove(traceList);
        }
        List<Object> result = new ArrayList<>();
        result.add(newGroups);
        result.add(groupsMap);

        return result;


    }*/

/*    public HashMap<Integer, List<List<XTrace>>> findGroups(List<XTrace> log){
        HashMap<Integer, List<List<XTrace>>> matches = new HashMap<>();
        int counter = 0;
        List<List<XTrace>> groups = new ArrayList<>();
        groups.add(log);
        while(!groups.isEmpty()){
            List<List<XTrace>> removed = new ArrayList<>();
            for(List<XTrace> group : groups){
                // Divide
                List<List<XTrace>> dividedGroups = groupBynthActivity(group,counter);
                for (List<XTrace> dividedGroup : dividedGroups) {
                    if (dividedGroup.size() <= 1) {
                        if (matches.containsKey(counter)) {
                            List<List<XTrace>> newGroup = matches.get(counter);
                            newGroup.add(dividedGroup);
                            matches.put(counter, newGroup);
                        } else {
                            List<List<XTrace>> newGroup = new ArrayList<>();
                            newGroup.add(dividedGroup);
                            matches.put(counter, newGroup);
                        }
                        removed.add(dividedGroup);
                    } else {
                        if (matches.containsKey(counter + 1)) {
                            List<List<XTrace>> newGroup = matches.get(counter + 1);
                            newGroup.add(dividedGroup);
                            matches.put(counter + 1, newGroup);
                        } else {
                            List<List<XTrace>> newGroup = new ArrayList<>();
                            newGroup.add(dividedGroup);
                            matches.put(counter + 1, newGroup);
                        }

                    }

                }


            }
            for (List<XTrace> gg : removed) {
                groups.remove(gg);
            }

            counter++;

        }
        return matches;

    }*/



/*    public TraceGroupMap findGroups(List<XTrace> log){
        TraceGroupMap map = new TraceGroupMap();
        int commons = 0;
        List<List<XTrace>> groups = groupByFirstActivity(log);
        while(!groups.isEmpty()){
            List<List<XTrace>> singleGroups = groups.stream().filter(g -> g.size() <= 1).collect(Collectors.toList());
            for(List<XTrace> singleGroup : singleGroups){
                XTrace trace = singleGroup.get(0);
                map.addGroup(trace, commons);
                groups.remove(singleGroup);
            }
            List<List<XTrace>> newGroups = groups.stream().filter(g -> g.size() > 1).collect(Collectors.toList());
            for(List<XTrace> newGroup : newGroups){
                List<List<XTrace>> divided = groupByFirstActivity(newGroup);
                for(List<XTrace> dividedGroup : divided){
                    dividedGroup.stream().forEach(t -> t.remove(0));
                    dividedGroup.stream().
                    groups.add(dividedGroup);
                }
            }
            System.out.println(commons);
            commons++;


        }
        return map;
    }*/

/*    public TraceGroupMap findGroups(List<XTrace> log) {
        TraceGroupMap map = new TraceGroupMap();
        int commons = 0;
        List<List<XTrace>> groups = groupByFirstActivity(log);

        while(!groups.isEmpty()){
            for(List<XTrace> group : groups){
                if(group.size() <= 1){
                    map.addGroup(group.get(0), commons);
                    groups.remove(group);
                } else{
                    List<List<XTrace>> newGroups = groupByFirstActivity(group);

                }
            }

        }
        return map;
    }*/

    public List<List<XTrace>> groupBynthActivity(List<XTrace> log, int n) {
        List<List<XTrace>> groups = new ArrayList<>();
        List<XTrace> multis = new ArrayList<>();
        for (XTrace trace : log) {
            if (trace.size() <= n) {
                List<XTrace> single = new ArrayList<>();
                single.add(trace);
                groups.add(single);
            } else {
                multis.add(trace);
            }
        }
        List<List<XTrace>> matchings = multis.stream().collect(Collectors
                .groupingBy(trace -> trace.get(n).getAttributes().get("Activity")))
                .values().stream().collect(Collectors.toList());

        for (List<XTrace> group : matchings) {
            groups.add(group);
        }
        return groups;
    }


    /**
     * Finds all variants in an event log by looking at the sequence of activities in each trace
     *
     * @param log Event log of type XLog containing one <log> element.
     * @return An object of type activityVariantMap which holds a map of the variants
     */
    public activityVariantMap findVariants(XLog log) {
        activityVariantMap variants = new activityVariantMap();
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


/*    public RelationToValuesMap relationToValues(XTrace trace, String attrKey, String operator,
                                                RelationToValuesMap relationToValuesMap) {
        for (XTrace trace : log) {
            // If variant has 1 event only
            if (trace.size() <= 1) {
                if (!operator.equals("COUNT")) return relationToValuesMap;
                // Creating the link
                XEvent event = trace.get(0);
                XAttribute eventAttr = event.getAttributes().get(attrKey);
                List<String> link = new ArrayList<>();
                String groupName = this.groups.getGroupName(trace,1);
                link.add(eventAttr.toString() + "_0_" + groupName);
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
                    String groupName = this.groups.getGroupName(trace, i+1);
                    link.add(sourceAttribute.toString() + "_" + i + "_" + groupName);
                    link.add(targetAttribute.toString() + "_" + (i + 1) + "_" + groupName);

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


            }
        }
        return relationToValuesMap;


    }*/


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
    public RelationToValuesMap relationToValues(XTrace trace, String attrKey, String operator,
                                                RelationToValuesMap relationToValuesMap) {
        // If trace has 1 event only
        if (trace.size() <= 1) {
            if (!operator.equals("COUNT")) return relationToValuesMap;
            // Creating the link
            XEvent event = trace.get(0);
            XAttribute eventAttr = event.getAttributes().get(attrKey);
            List<String> link = new ArrayList<String>();
            String groupName = this.groups.getGroupName(trace, 0);

            link.add(eventAttr.toString() + "_0_" + "(" + groupName + ")");
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
                String sourceGroupName = this.groups.getGroupName(trace, i);
                String targetGroupName = this.groups.getGroupName(trace, i+1);

                link.add(sourceAttribute.toString() + "_" + (i) + "_" + "(" + sourceGroupName + ")");
                link.add(targetAttribute.toString() + "_" + (i + 1) + "_" + "(" + targetGroupName + ")");

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

