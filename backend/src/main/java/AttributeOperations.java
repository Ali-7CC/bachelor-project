import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeTimestamp;

/**
 * Defines the operations that can be done between log attribute values based on the attribute type.
 */
public class AttributeOperations {

    public static double sum(XAttribute attr1, XAttribute attr2) {
        double result = Double.NaN;

        // int type
        if ((attr1 instanceof XAttributeDiscrete) && attr2 instanceof XAttributeDiscrete) {
            result = ((XAttributeDiscrete) attr1).getValue() + ((XAttributeDiscrete) attr2).getValue();
        }

        // float type
        else if ((attr1 instanceof XAttributeContinuous) && attr2 instanceof XAttributeContinuous) {
            result = ((XAttributeContinuous) attr1).getValue() + ((XAttributeContinuous) attr2).getValue();
        }

        // date type
        else if ((attr1 instanceof XAttributeTimestamp) && (attr2 instanceof XAttributeTimestamp)) {
            result = ((XAttributeTimestamp) attr1).getValueMillis() + ((XAttributeTimestamp) attr2).getValueMillis();
        }
        return result;
    }

    public static double diff(XAttribute attr1, XAttribute attr2) {
        double result = Double.NaN;
        // int type
        if ((attr1 instanceof XAttributeDiscrete) && attr2 instanceof XAttributeDiscrete) {
            result = ((XAttributeDiscrete) attr1).getValue() - ((XAttributeDiscrete) attr2).getValue();
        }

        // float type
        else if ((attr1 instanceof XAttributeContinuous) && attr2 instanceof XAttributeContinuous) {
            result = ((XAttributeContinuous) attr1).getValue() - ((XAttributeContinuous) attr2).getValue();
        }

        // date type
        else if ((attr1 instanceof XAttributeTimestamp) && (attr2 instanceof XAttributeTimestamp)) {
            result = ((XAttributeTimestamp) attr1).getValueMillis() - ((XAttributeTimestamp) attr2).getValueMillis();
        }
        return Math.abs(result);
    }

}
