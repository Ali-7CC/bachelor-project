import java.util.*;


/**
 * Class that represents the set of mappings that have the following form:
 * (eventName 1, eventName 2) --> [Double 1, Double 2, ..]
 * The semantic of the double depends two factors:
 * 1) The chosen event attribute, e.g. time:timestamp
 * 2) The operation applied to the values of the chosen attribute, e.g. difference
 */
public class RelationToValuesMap {
    // The HashMap made by the LogPreprocessor
    public HashMap<List<String>, List<Double>> map;
    // The attribute key and operator that were used to create the HashMap
    String attrKey;
    String operator;

    public RelationToValuesMap(String attrKey, String operator) {
        this.attrKey = attrKey;
        this.operator = operator;
        this.map = new HashMap<>();
    }

    /**
     * Sums the list of values associated with each relation in this.map
     *
     * @return Object of type RelationToAggregatedValueMap that holds the relations and their aggregated values i.a.
     */
    public RelationToAggregatedValueMap aggregateSum() {
        HashMap<List<String>, Double> result = new HashMap<>();
        // Iterating through entries of this.map
        for (Map.Entry<List<String>, List<Double>> entry : this.map.entrySet()) {
            // Populating the result HashMap with the relation and its summed values
            result.put(entry.getKey(), entry.getValue().stream().mapToDouble(v -> v).sum());
        }
        // Putting the result in a RelationToAggregatedValueMap object and returning it
        RelationToAggregatedValueMap aggregatedValueMap = new RelationToAggregatedValueMap(result, this.attrKey,
                this.operator, "SUM");
        return aggregatedValueMap;
    }

    /**
     * Finds the minimum value for each relation in this.map
     *
     * @return Object of type RelationToAggregatedValueMap that holds the relations and their aggregated values i.a.
     */
    public RelationToAggregatedValueMap aggregateMin() {
        HashMap<List<String>, Double> result = new HashMap<>();
        // Iterating through entries of this.map
        for (Map.Entry<List<String>, List<Double>> entry : this.map.entrySet()) {
            // Populating the result HashMap with the relation and its minimum value
            result.put(entry.getKey(), Collections.min(entry.getValue()));
        }
        // Putting the result in a RelationToAggregatedValueMap object and returning it
        RelationToAggregatedValueMap aggregatedValueMap = new RelationToAggregatedValueMap(result, this.attrKey,
                this.operator, "MIN");
        return aggregatedValueMap;
    }

    /**
     * Finds the maximum value for each relation in the HashMap.
     *
     * @return Object of type RelationToAggregatedValueMap that holds the relations and their aggregated values i.a.
     */
    public RelationToAggregatedValueMap aggregateMax() {
        HashMap<List<String>, Double> result = new HashMap<>();
        // Iterating through entries of this.map
        for (Map.Entry<List<String>, List<Double>> entry : this.map.entrySet()) {
            // Populating the result HashMap with the relation and its maximum value
            result.put(entry.getKey(), Collections.max(entry.getValue()));
        }
        // Putting the result in a RelationToAggregatedValueMap object and returning it
        RelationToAggregatedValueMap aggregatedValueMap = new RelationToAggregatedValueMap(result, this.attrKey,
                this.operator, "MAX");
        return aggregatedValueMap;
    }

    public RelationToAggregatedValueMap aggregateAvg() {
        HashMap<List<String>, Double> result = new HashMap<>();
        // Iterating through entries of this.map
        for (Map.Entry<List<String>, List<Double>> entry : this.map.entrySet()) {
            // Populating the result HashMap with the relation and its average value
            result.put(entry.getKey(), entry.getValue().stream().mapToDouble(a -> a).average().orElse(Double.NaN));
        }
        // Putting the result in a RelationToAggregatedValueMap object and returning it
        RelationToAggregatedValueMap aggregatedValueMap = new RelationToAggregatedValueMap(result, this.attrKey,
                this.operator, "AVG");
        return aggregatedValueMap;


    }


}
