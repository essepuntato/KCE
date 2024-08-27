package it.essepuntato.facility.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Silvio Peroni
 */
public class CollectionFacility {
	
	@SuppressWarnings("unchecked")
	public static Collection copy(Collection c) {
        Collection result = null;        
		try {
			result = c.getClass().newInstance();
			Iterator ite = c.iterator();
	        while (ite.hasNext()) {
	            result.add(ite.next());
	        }
		} catch (InstantiationException e) {
			// Do nothing
			System.out.println("No new collection created.");
		} catch (IllegalAccessException e) {
			// Do nothing
			System.out.println("No new collection created.");
		}
        
        return result;
    }
}
