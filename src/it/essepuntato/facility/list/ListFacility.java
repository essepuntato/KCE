package it.essepuntato.facility.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Silvio Peroni
 */
public class ListFacility {
    public static boolean containsTheSameStringValue(List<String> l, String s) {
        boolean result = false;
        
        Iterator<String> ite = l.iterator();
        while (ite.hasNext() && !result) {
            String cur = ite.next();
            if (cur.equals(s)) {
                result = true;
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static boolean containsTheSameObject(List l, Object o) {
        boolean result = false;
        
        Iterator ite = l.iterator();
        while (ite.hasNext()) {
            Object cur = ite.next();
            if (cur == o) {
                result = true;
                break;
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static boolean containsAllTheObjects(List l, List objects) {
        boolean result = true;
        
        Iterator ite = objects.iterator();
        while (ite.hasNext() && result) {
            Object o = ite.next();
            result = ListFacility.containsTheSameObject(l, o);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static boolean containsAtLeastOneObject(List l, List objects) {
        boolean result = false;
        
        Iterator ite = objects.iterator();
        while (ite.hasNext() && !result) {
            Object o = ite.next();
            result = ListFacility.containsTheSameObject(l, o);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List addUnique(List l, Object o) {
        boolean isInList = false;
        List result = new ArrayList();
        
        Iterator ite = l.iterator();
        while (ite.hasNext()) {
            Object cur = ite.next();
            result.add(cur);
            if (cur == o) {
                isInList = true;
            }
        }
        
        if (!isInList) {
            result.add(o);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List addAllUnique(List l, List add) {
        List result = new ArrayList();
        result.addAll(l);
        
        Iterator ite = add.iterator();
        while (ite.hasNext()) {
            Object cur = ite.next();
            result = ListFacility.addUnique(result, cur);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List copy(List l) {
        List result = new ArrayList();
        
        Iterator ite = l.iterator();
        while (ite.hasNext()) {
            result.add(ite.next());
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List moveInFirstPosition(List l, Object o) {
        List result = new ArrayList();
        
        if (ListFacility.containsTheSameObject(l, o)) {
            result.add(o);
            Iterator ite = l.iterator();
            while (ite.hasNext()) {
                Object cur = ite.next();
                if (cur != o) {
                    result.add(cur);
                }
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List difference(List one, List two) {
        List result = new ArrayList();
        
        Iterator iteOne = one.iterator();
        Iterator iteTwo = two.iterator();
        while (iteOne.hasNext() || iteTwo.hasNext()) {
            Object oOne = null;
            Object oTwo = null;
            if (iteOne.hasNext()) {
                oOne = iteOne.next();
            }
            if (iteTwo.hasNext()) {
                oTwo = iteTwo.next();
            }
            if (oOne != null && !ListFacility.containsTheSameObject(two, oOne)) {
                result.add(oOne);
            }
            if (oTwo != null && !ListFacility.containsTheSameObject(one, oTwo)) {
                result.add(oTwo);
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List intersect(List one, List two) {
        List result = new ArrayList();
        
        Iterator iteOne = one.iterator();
        while (iteOne.hasNext()) {
            Object current = iteOne.next();
            if (ListFacility.containsTheSameObject(two, current)) {
                result.add(current);
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List subListFrom(List l, int index) {
    	List result = new ArrayList();
    	int size = l.size();
    	
    	for (int i = index; i < size; i++) {
    		result.add(l.get(i));
    	}
    	
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static List subListTo(List l, int index) {
    	List result = new ArrayList();
    	int size = l.size();
    	
    	for (int i = 0; i < size && i < index; i++) {
    		result.add(l.get(i));
    	}
    	
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static int indexOfByOccurrence(List l, Object item, int occurrence) {
    	int index = -1;
		int currentOccurrence = 0;
		List tmp;
		
		do {
			index++;
			tmp = ListFacility.subListFrom(l, index);
			int curIndex = tmp.indexOf(item);
			
			if (curIndex == -1) {
				index = -1;
			} else {
				index += curIndex;
				currentOccurrence++;
			}
			
		} while (index > -1 && currentOccurrence < occurrence) ;
    	
    	return index;
    }
}
