package it.essepuntato.semanticweb.kce.algorithm;

import it.essepuntato.facility.list.ListFacility;
import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.facility.math.MathFacility;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  <p>
 *      This class implements the new version of the density algorithm. The goal
 *      of this approach is to identify what is the density of all the categories
 *      of an ontology basing the result on two particular value:
 *  </p>
 *  <ul>
 *      <li>
 *          <p>
 *              working out the <i>global density</i> that represents the density of
 *              a category taking into consideration all the ontology;
 *          </p>
 *      </li>
 *      <li>
 *          <p>
 *              working out the <i>local density</i> that represents the density of
 *              a category calculated using the global density values of the neighbour
 *              categories (the identification of the category neighbours depends on
 *              a range value and a ratio).
 *          </p>
 *      </li>
 *  </ul>
 * 
 * @author Silvio Peroni
 * @version beta 3
 */
public class Density implements IAlgorithm {
    
    //Paramenters
    private double globalDensityWeight = 1.0;
    private double localDensityWeight = 1.0;
    private double directSubclassesWeight = 2.0;
    private double indirectSubclassesWeight = 0.0;
//    private double categoryWeight = 2.0;
    private double propertyWeight = 3.0;
    private double instanceWeight = 1.0;
    private int range = 1;
    private double ratio = 0.1;
    private double threshold = 0.5;
    private String name = "";
    private double globalDensityWeightLocally = 0.5;
    private boolean debug = true;

    public Density(String string) {
        this.name = string;
    }

    public Map<String, Object> evaluate(HTaxonomy ht, Map<String, String> params) {
        System.out.println("\n-- Density Algorithm: BEGIN");
        
        //HTaxonomy t = ht.clone();
        HTaxonomy t = ht;
        
        this.globalDensityWeight = new Double(params.get("globalDensityWeight")).doubleValue();
        this.localDensityWeight = new Double(params.get("localDensityWeight")).doubleValue();
//      this.categoryWeight = new Double(params.get("categoryWeight")).doubleValue();
        this.directSubclassesWeight = new Double(params.get("directSubclassesWeight")).doubleValue();
        this.indirectSubclassesWeight = new Double(params.get("indirectSubclassesWeight")).doubleValue();
        this.propertyWeight = new Double(params.get("propertyWeight")).doubleValue();
        this.instanceWeight = new Double(params.get("instanceWeight")).doubleValue();
        this.range = new Integer(params.get("range")).intValue();
        this.ratio = new Double(params.get("ratio")).doubleValue();
        this.threshold = new Double(params.get("threshold")).doubleValue();
        this.globalDensityWeightLocally = new Double(params.get("globalDensityWeightLocally")).doubleValue();
        
        if (debug) System.out.println("Density Algorithm:");
        if (debug) System.out.println("\tglobalDensityWeight = " + this.globalDensityWeight);
        if (debug) System.out.println("\tlocalDensityWeight = " + this.localDensityWeight);
//      if (debug) System.out.println("\tcategoryWeight = " + this.categoryWeight);
        if (debug) System.out.println("\tdirectSubclassesWeight = " + this.directSubclassesWeight);
        if (debug) System.out.println("\tindirectSubclassesWeight = " + this.indirectSubclassesWeight);
        if (debug) System.out.println("\tpropertyWeight = " + this.propertyWeight);
        if (debug) System.out.println("\tinstanceWeight = " + this.instanceWeight);
        if (debug) System.out.println("\trange = " + this.range);
        if (debug) System.out.println("\tratio = " + this.ratio);
        if (debug) System.out.println("\tthreshold = " + this.threshold);
        if (debug) System.out.println("\tglobalDensityWeightLocally = " + this.globalDensityWeightLocally);
        
        if (debug) System.out.println("Density Algorithm: working out the global density");
        Hashtable<Category,Double> globalDensity = this.calculateGlobalDensity(t);
        
        if (debug) System.out.println("Density Algorithm: working out the local density");
        Hashtable<Category,Double> localDensity = this.calculateLocalDensity(t, globalDensity);
        
        if (debug) System.out.println("Density Algorithm: working out the final density");
        Hashtable<Category,Double> density = this.calculateDensity(t, globalDensity, localDensity);
        
        Hashtable<String, Object> result = new Hashtable<String,Object>();
        result.put("taxonomy", t);
        
        System.out.println("-- Density Algorithm: END\n");
        return result;
    }

    private Hashtable<Category,Double> calculateDensity(
            HTaxonomy t, 
            Hashtable<Category, Double> globalDensity, 
            Hashtable<Category, Double> localDensity) {
        Hashtable<Category,Double> result = new Hashtable<Category, Double>();
        
        Iterator<Category> ite = globalDensity.keySet().iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            try {
                double curGlobalDensity = globalDensity.get(c).doubleValue();
                double curLocalDensity = localDensity.get(c).doubleValue();

                double curDensityTmp = 
                        (curGlobalDensity * this.globalDensityWeight) 
                        + 
                        (curLocalDensity * this.localDensityWeight);
                double value = 
                        MathFacility.normalize(
                        1.0, 
                        curDensityTmp, 
                        new Double(this.globalDensityWeight + this.localDensityWeight).doubleValue());

                t.addInfo(c, "DensityIs", value > this.threshold ? "yes" : "no");
                if (debug) System.out.println("\tfinal density for '" + c.getName() + "':" + value);
                t.addInfo(c, "DensityFinal", Double.toString(value));
                result.put(c, value);
            } catch (NoCategoryException ex) {
                System.err.println("[Density: calculateGlobalDensitty] ERROR - The category '" + 
                        c.getName() + "' isn't in the taxonomy.");
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private Hashtable<Category,Double> calculateGlobalDensity(HTaxonomy t) {
        Hashtable<Category,Double> result = new Hashtable<Category, Double>();
        
        double maxGlobalDensity = Double.MIN_VALUE;
        
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category cur = ite.next();
            if (debug) System.out.println("Considering category '" + cur.getName() +"'");
            
            /* The root category hasn't global density */
            if (cur == t.getRoot()) {
                result.put(cur, new Double(0.0));
            } 
            else {
                try {
                    double curDensityTmp = 
                            /*(t.getDirectSubCategories(cur).size() * this.categoryWeight) +*/
                    		(t.getDirectSubCategories(cur).size() * this.directSubclassesWeight) +
                    		(
                    				(t.getAllSubCategories(cur).size() - t.getDirectSubCategories(cur).size()) * 
                    				this.indirectSubclassesWeight) +
                            (t.getPropertiesByDomain(cur).size() * this.propertyWeight) +
                            (t.getDirectInstances(cur).size() * this.instanceWeight);

                    /* Normalized by max.num. sub categories (bad case: flat ontology), 
                     * max.num.properties, max.num.instances */
                    double max = 
                    		/*((t.getAllCategories().size() - 1) * this.categoryWeight) +*/
                            ((t.getAllCategories().size() - 1) * 
                            		(this.directSubclassesWeight + this.indirectSubclassesWeight)) + //max of subcategories
                            (t.getAllProperties().size() * this.propertyWeight) + //max of properties
                            (t.getAllInstances().size() * this.instanceWeight); //max of instances

                    double curDensity = MathFacility.normalize(1.0, curDensityTmp, max);

                    result.put(cur, new Double(curDensity));
                    if (curDensity > maxGlobalDensity) {
                        maxGlobalDensity = curDensity;
                    }

                } catch (NoCategoryException ex) {
                    System.err.println("[Density: calculateGlobalDensity] ERROR - The category '" + 
                            cur.getName() + "' isn't in the taxonomy.");
                    ex.printStackTrace();
                }
            }
        }
        
        /* I normalize the global density for each category */
        Iterator<Category> iteGlobal = result.keySet().iterator();
        while (iteGlobal.hasNext()) {
            Category key = iteGlobal.next();
            try {
                double normalizedGlobal = 
                        MathFacility.normalize(1.0, result.get(key).doubleValue(), maxGlobalDensity);
                result.put(key, normalizedGlobal);
                
                if (debug) System.out.println("\tglobal density for '" + key.getName() + "':" + normalizedGlobal);
                t.addInfo(key, "DensityGlobal", Double.toString(normalizedGlobal));
                
            } catch (NoCategoryException ex) {
                System.err.println("[Density: calculateGlobalDensity] ERROR - The category '" + 
                        key.getName() + "' isn't in the taxonomy.");
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private Hashtable<Category, Double> calculateLocalDensity(
            HTaxonomy t, Hashtable<Category, Double> globalDensity) {
        Hashtable<Category,Double> result = new Hashtable<Category, Double>();
        double maxLocalDensity = 0.0;
        
        Iterator<Category> ite = globalDensity.keySet().iterator();
        while (ite.hasNext()) {
            Category cur = ite.next();
            
            /* The root category hasn't the local density */
            if (cur == t.getRoot()) {
                result.put(cur, 0.0);
            }
            else {
                double curDensity = globalDensity.get(cur).doubleValue();
                double maxDensity = this.findMaxDensity(
                        t, cur, globalDensity, this.range, this.ratio, new ArrayList());
                double curLocalDensity = 0.0;
                if (maxDensity != 0.0) {
                    curLocalDensity = 
                            (curDensity/maxDensity) 
                            + 
                            (this.globalDensityWeightLocally * curDensity);
                }
                result.put(cur, curLocalDensity);

                if (curLocalDensity > maxLocalDensity) {
                    maxLocalDensity = curLocalDensity;
                }
            }
        }
        
        ite = result.keySet().iterator();
    	while (ite.hasNext()) {
            Category key = ite.next();
            double normalizedLocalDensity = 
            	(maxLocalDensity == 0.0 ? 
            			0.0 :
        				MathFacility.normalize(1.0, result.get(key).doubleValue(), maxLocalDensity)
				);
            
            result.put(key, normalizedLocalDensity);
            if (debug) System.out.println("\tlocal density for '" + key.getName() + "':" + normalizedLocalDensity);
            try {
                t.addInfo(key, "DensityLocal", Double.toString(normalizedLocalDensity));
            } catch (NoCategoryException ex) {
                System.err.println("[Density: calculateLocalDensity] ERROR - The category '" + 
                        key.getName() + "' isn't in the taxonomy.");
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private double findMaxDensity(
            HTaxonomy t, 
            Category cur,
            Hashtable<Category,Double> globalDensity, 
            int range, 
            double ratio, 
            ArrayList<Category> viewed) {
        double currentRatio = 1.0 - (ratio * new Double(this.range - range).doubleValue());
        double result = (currentRatio < 0.0 ? 0.0 : currentRatio) * globalDensity.get(cur).doubleValue();
        viewed.add(cur);
        
        if (range != 0) {
            try {
                ArrayList<Category> neighbours = new ArrayList<Category>();
                neighbours.addAll(t.getDirectSubCategories(cur));
                neighbours.addAll(t.getDirectSuperCategories(cur));
                neighbours.removeAll(viewed);
                
                Iterator<Category> ite = neighbours.iterator();
                while (ite.hasNext()) {
                    Category category = ite.next();
                    ArrayList<Category> newViewed = (ArrayList<Category>) ListFacility.copy(viewed);
                    double categoryMax = 
                            this.findMaxDensity(t, category, globalDensity, range - 1, ratio, newViewed);
                    if (categoryMax > result) {
                        result = categoryMax;
                    }
                }
            } catch (NoCategoryException ex) {
                System.err.println("[Density: findMaxDensity] ERROR - The category '" + 
                        cur.getName() + "' isn't in the taxonomy.");
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
