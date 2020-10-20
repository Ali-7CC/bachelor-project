import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TraceGroups {
    HashMap<Integer, List<List<XTrace>>> map;


    public String getGroupName(XTrace trace, int position) {
        String groupName = null;
        if (map.containsKey(position)) {
            List<List<XTrace>> traceGroups = map.get(position);
            for (List<XTrace> traceGroup : traceGroups) {
                if (traceGroup.contains(trace)) {
                    List<String> groupNameList = new ArrayList<>();
                    groupNameList.add("G");
                    groupNameList.add(String.valueOf(position));
                    XTrace t = traceGroup.get(0);
                    for (int i = 0; i <= position; i++) {
                        groupNameList.add(t.get(i).getAttributes().get("Activity").toString());
                    }
                    groupName = groupNameList.stream().collect(Collectors.joining());
                }
            }
            if(groupName == null){
                groupName = trace.getAttributes().get("concept:name").toString();
            }
        } else {
            groupName = trace.getAttributes().get("concept:name").toString();
        }
        return groupName;

    }
}