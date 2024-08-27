package it.essepuntato.facility.set;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Silvio Peroni
 */
public class SetFacility {
    public static Set intersect(Set one, Set two) {
        Set result = new HashSet();

        Set selected = null;
        boolean firstSelected = true;
        if (one.size() < two.size()) {
            selected = one;
        } else {
            selected = two;
            firstSelected = false;
        }

        Iterator ite = selected.iterator();
        while (ite.hasNext()) {
            Object current = ite.next();
            if ((firstSelected ? two : one).contains(current)) {
                result.add(current);
            }
        }

        return result;
    }
}
