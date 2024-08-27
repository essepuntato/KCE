package it.essepuntato.facility.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Silvio Peroni
 */
public class MathFacility {
    
    public static int intCeil(double i) {
        if (i % 2.0 == 0) {
            return new Double(i).intValue();
        }
        else {
            return new Double(i + 1.0).intValue();
        }
    }
    
    public static int intFloor(double i) {
        return new Double(i).intValue();
    }
    
    public static double average(List<Integer> l) {
        double total = 0;
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            total += ite.next().intValue();
        }
        
        return total / new Double(l.size()).doubleValue();
    }
    
    public static double averageDouble(List<Double> l) {
        double total = 0.0;
        
        Iterator<Double> ite = l.iterator();
        while (ite.hasNext()) {
            total += ite.next().doubleValue();
        }
        
        return total / new Double(l.size()).doubleValue();
    }
    
    public static double normalize(double newBase, double oldValue, double oldBase) {
        return (oldValue / oldBase) * newBase;
    }
    
    public static Integer maxInteger(List<Integer> l) {
        Integer result = null;
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer cur = ite.next();
            if (result == null || cur.intValue() > result.intValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static Integer minInteger(List<Integer> l) {
        Integer result = null;
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer cur = ite.next();
            if (result == null || cur.intValue() < result.intValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static Long maxLong(List<Long> l) {
        Long result = null;
        
        Iterator<Long> ite = l.iterator();
        while (ite.hasNext()) {
            Long cur = ite.next();
            if (result == null || cur.intValue() > result.intValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static Long minLong(List<Long> l) {
        Long result = null;
        
        Iterator<Long> ite = l.iterator();
        while (ite.hasNext()) {
            Long cur = ite.next();
            if (result == null || cur.intValue() < result.intValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static Double maxDouble(List<Double> l) {
        Double result = null;
        
        Iterator<Double> ite = l.iterator();
        while (ite.hasNext()) {
            Double cur = ite.next();
            if (result == null || cur.doubleValue() > result.doubleValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static Double minDouble(List<Double> l) {
        Double result = null;
        
        Iterator<Double> ite = l.iterator();
        while (ite.hasNext()) {
            Double cur = ite.next();
            if (result == null || cur.doubleValue() < result.doubleValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static List<Integer> subTo(Integer value, List<Integer> l) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            result.add(new Integer(ite.next().intValue() - value.intValue()));
        }
        
        return result;
    }
    
    public static List<Integer> addTo(Integer value, List<Integer> l) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            result.add(new Integer(ite.next().intValue() + value.intValue()));
        }
        
        return result;
    }
    
    public static List<Integer> cutLesserEqualThan(List<Integer> l, Integer cut) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer i = ite.next();
            if (i.intValue() > cut.intValue()) {
                result.add(i);
            }
        }
        
        return result;
    }
    
    public static List<Integer> cutLesserThan(List<Integer> l, Integer cut) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer i = ite.next();
            if (i.intValue() >= cut.intValue()) {
                result.add(i);
            }
        }
        
        return result;
    }
    
    public static List<Integer> cutGreaterEqualThan(List<Integer> l, Integer cut) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer i = ite.next();
            if (i.intValue() < cut.intValue()) {
                result.add(i);
            }
        }
        
        return result;
    }
    
    public static List<Integer> cutGreaterThan(List<Integer> l, Integer cut) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer i = ite.next();
            if (i.intValue() <= cut.intValue()) {
                result.add(i);
            }
        }
        
        return result;
    }
    
    public static Integer nearestTo(List<Integer> l, Integer to) {
        Integer result = null;
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            Integer cur = ite.next();
            if (result == null || Math.abs(cur.intValue() - to.intValue()) < result.intValue()) {
                result = cur;
            }
        }
        
        return result;
    }
    
    public static int sumInteger(List<Integer> l) {
        int result = 0;
        
        Iterator<Integer> ite = l.iterator();
        while (ite.hasNext()) {
            result += ite.next().intValue();
        }
        
        return result;
    }
    
    public static double sumDouble(List<Double> l) {
        double result = 0;
        
        Iterator<Double> ite = l.iterator();
        while (ite.hasNext()) {
            result += ite.next().doubleValue();
        }
        
        return result;
    }
}
