package it.essepuntato.facility.map;

import it.essepuntato.facility.math.MathFacility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Silvio Peroni
 */
public class MapFacility {
    
    
    
    public static Object getKeyWithMaxIntegerValue(Map<Object,Integer> m) {
        Object result = null;
        int max = Integer.MIN_VALUE;
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            int value = m.get(o).intValue();
            if (result == null || value > max) {
                result = o;
                max = value;
            }
        }
        
        return result;
    }
    
    public static Object getKeyWithMaxDoubleValue(Map<Object,Double> m) {
        Object result = null;
        double max = Double.MIN_VALUE;
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            double value = m.get(o).doubleValue();
            if (result == null || value > max) {
                result = o;
                max = value;
            }
        }
        
        return result;
    }
    
    public static List getKeysWithMaxDoubleValue(Map<Object,Double> m) {
        ArrayList result = new ArrayList();
        
        double max = MathFacility.maxDouble(new ArrayList<Double>(m.values()));
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            double value = m.get(o).doubleValue();
            if (value == max) {
                result.add(o);
            }
        }
        
        return result;
    }
    
    public static List getKeysWithDoubleValueGreaterThan(Map<Object,Double> m, Double threshold) {
        ArrayList result = new ArrayList();
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            double value = m.get(o).doubleValue();
            if (value >= threshold.doubleValue()) {
                result.add(o);
            }
        }
        
        return result;
    }
    
    public static Object getKeyWithMinIntegerValue(Map<Object,Integer> m) {
        Object result = null;
        int min = Integer.MAX_VALUE;
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            int value = m.get(o).intValue();
            if (result == null || value < min) {
                result = o;
                min = value;
            }
        }
        
        return result;
    }
    
    public static Object getKeyWithMinDoubleValue(Map<Object,Double> m) {
        Object result = null;
        double min = Double.MAX_VALUE;
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            double value = m.get(o).doubleValue();
            if (result == null || value < min) {
                result = o;
                min = value;
            }
        }
        
        return result;
    }
    
    public static List getKeysWithMinDoubleValue(Map<Object,Double> m) {
        ArrayList result = new ArrayList();
        
        double min = MathFacility.minDouble(new ArrayList<Double>(m.values()));
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            double value = m.get(o).doubleValue();
            if (value == min) {
                result.add(o);
            }
        }
        
        return result;
    }
    
    public static List getKeysWithDoubleValueLesserThan(Map<Object,Double> m, Double threshold) {
        ArrayList result = new ArrayList();
        
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            double value = m.get(o).doubleValue();
            if (value <= threshold.doubleValue()) {
                result.add(o);
            }
        }
        
        return result;
    }
    
    public static void removeAllKeysFromMap(Collection keys, Map m) {
        Iterator<Object> ite = keys.iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            if (MapFacility.isKeyOf(o, m)) {
                m.remove(o);
            }
        }
    }
    
    public static boolean isKeyOf(Object key, Map m) {
        Iterator<Object> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            if (key == o) {
                return true;
            }
        }
                
        return false;
    }
    
    public static Map copy(Map m) {
        Map result = new Hashtable();
        
        Iterator ite = m.keySet().iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            result.put(o, m.get(o));
        }
        
        return result;
    }
}
