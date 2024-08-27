package it.essepuntato.facility.string;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silvio Peroni
 */
public class StringFacility {
    public static List<Integer> indexOf(String string, String pattern) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        int size = string.length();
        int index = 0;
        boolean hasNext = (size == 0 ? false : true);
        
        while (hasNext) {
            int i = string.indexOf(pattern, index);
            if (i == -1) {
                hasNext = false;
            }
            else {
                index = i + 1;
                if (index > size) {
                    hasNext = false;
                }
                result.add(new Integer(i));
            }
        }
        
        return result;
    }
}
