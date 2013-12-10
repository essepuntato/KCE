package it.essepuntato.semanticweb.kce.algorithm;

import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.facility.list.ListFacility;
import it.essepuntato.facility.map.MapFacility;
import it.essepuntato.facility.math.MathFacility;
import it.essepuntato.facility.string.StringFacility;

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  <p>
 *      This class implements the new version of the natural category algorithm. The goal of
 *      this process is to identify what are the possible natural categories basing on three
 *      constraints:
 *  </p>
 *  <ul>
 *      <li>
 *          <p>
 *              considering as <q>natural</q> only one category of the same branch;
 *        </p>
 *      </li>
 *      <li>
 *          <p>
 *              every category has a particular basic level. This level is worked out through a
 *              recursive approach;
 *          </p>
 *      </li>
 *      <li>
 *          <p>
 *              evaluating (euristic approach) the goodness of the category name (remembering that
 *              usually a natural category has a simple name).
 *          </p>
 *      </li>
 *  </ul>
 * 
 * @author Silvio Peroni
 * @version beta 2
 */
public class NaturalCategory implements IAlgorithm {
    
    //Parameters
    private int levelWeight = 1;
    private int nameWeight = 1;
    private double threshold = 0.7;
    private double compoundRatio = 0.3;
    private String name = "";
    private boolean debug = false;

    public NaturalCategory(String string) {
        this.name = string;
    }

    public Map<String, Object> evaluate(HTaxonomy ht, Map<String, String> params) {
        System.out.println("\n-- Natural Category Algorithm: BEGIN");
        
        //HTaxonomy t = ht.clone();
        HTaxonomy t = ht;
        
        this.levelWeight = new Integer(params.get("levelWeight")).intValue();
        this.nameWeight = new Integer(params.get("nameWeight")).intValue();
        this.threshold = new Double(params.get("threshold")).doubleValue();
        this.compoundRatio = new Double(params.get("compoundRatio")).doubleValue();
        
        if (debug) System.out.println("Natural Category Algorithm:");
        if (debug) System.out.println("\tlevelWeight = " + this.levelWeight);
        if (debug) System.out.println("\tnameWeight = " + this.nameWeight);
        if (debug) System.out.println("\tthreshold = " + this.threshold);
        if (debug) System.out.println("\tcompoundRatio = " + this.compoundRatio);
        
        if (debug) System.out.println("Natural Category Algorithm: finding all the paths");
        ArrayList<ArrayList<Category>> paths = 
                this.calculateAllPaths(t,t.getRoot(),new ArrayList<Category>());
        
        if (debug) System.out.println("Natural Category Algorithm: calculating the basic levels");
        Hashtable<Category, Double> basicLevels = 
                this.calculateBasicLevels(t, paths);
        
        if (debug) { System.out.println("Natural Category Algorithm: calculating the name simplicity of" +
                "all the taxonomy categories"); }
        Hashtable<Category, Double> nameGoodness = this.calculateNameGoodness(t);
        
        if (debug) System.out.println("Natural Category Algorithm: finding all the natural categories");
        HTaxonomy resultTaxonomy = 
                this.findNaturalCategories(t, basicLevels, nameGoodness);
        
        Hashtable<String, Object> result = new Hashtable<String,Object>();
        result.put("taxonomy", resultTaxonomy);
        
        System.out.println("-- Natural Category Algorithm: END\n");
        return result;
    }

    private ArrayList<ArrayList<Category>> calculateAllPaths(
            HTaxonomy t, Category start, ArrayList<Category> path) {
        
        ArrayList<ArrayList<Category>> result = new ArrayList<ArrayList<Category>>();
        
        if (!path.contains(start)) {
        	try {
                ArrayList<Category> tmp = (ArrayList<Category>) ListFacility.copy(path);
                tmp.add(start);

                Set<Category> subCategories = t.getDirectSubCategories(start);
                /* Base case */
                if (subCategories.isEmpty()) {
                    result.add(tmp);
                } else {
                    Iterator<Category> subCategoryIterator = subCategories.iterator();
                    while (subCategoryIterator.hasNext()) {
                        Category sub = subCategoryIterator.next();

                        ArrayList<ArrayList<Category>> subResult = 
                                this.calculateAllPaths(t, sub, (ArrayList<Category>) ListFacility.copy(tmp));

                        result.addAll(subResult);
                    }
                }
            } catch (NoCategoryException ex) {
                if (debug) { System.out.println("[NaturalCategory: calculateAllPaths] ERROR - The '" 
                            + start.getName() + "' isn't in the" +
                        "taxonomy"); }
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private Hashtable<Category, Double> calculateBasicLevels(
            HTaxonomy t, ArrayList<ArrayList<Category>> paths) {
        
        Hashtable<Category, Double> result = new Hashtable<Category, Double>();
        
        /* I initialize the result hash table */
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category cur = (Category) ite.next();
            result.put(cur, new Double(0.0));
        }
        
        /* I work out all the basic level weights */
        double max = 0.0;
        Iterator<ArrayList<Category>> pathsIterator = paths.iterator();
        while (pathsIterator.hasNext()) {
            ArrayList<Category> path = pathsIterator.next();
            
            int size = path.size();
            
            /* The path must have at least three categories */
            if (size != 1 && size != 2) {
                /* Even */
                if (size % 2 == 0) {
                    Category categoryOne = path.get((size / 2) - 1);
                    double valueOne = result.get(categoryOne).doubleValue() + 1.0;
                    result.put(categoryOne, new Double(valueOne));
                    if (valueOne > max) {
                        max = valueOne;
                    }
                    
                    Category categoryTwo = path.get(size / 2);
                    double valueTwo = result.get(categoryTwo).doubleValue() + 1.0;
                    result.put(categoryTwo, new Double(valueTwo));
                    if (valueTwo > max) {
                        max = valueTwo;
                    }
                }
                /* Odd */
                else {
                    Category category = path.get(((size + 1) / 2) - 1);
                    double value = result.get(category).doubleValue() + 1.0;
                    result.put(category, new Double(value));
                    if (value > max) {
                        max = value;
                    }
                }                
            }
        }
        
        /* I normalize all the results from 0 to 1 */
        Iterator<Category> resultIterator = result.keySet().iterator();
    	while (resultIterator.hasNext()) {
            Category key = resultIterator.next();
            try {
                double newValue = 
                	(max == 0.0 ? 
                			0.0 :
	                        MathFacility.normalize(
		                        1.0, 
		                        result.get(key).doubleValue(), 
		                        max)
                    );
                result.put(key, new Double(newValue));
                t.addInfo(key, "NaturalCategoryBasicLevel", Double.toString(newValue));
                if (debug) System.out.println("\tbasic level weight for '" + key.getName() + "': " + newValue);
                
            } catch (NoCategoryException ex) {
                if (debug) { System.out.println("[NaturalCategory: calculateBasicLevel] ERROR - The '" 
                        + key.getName() + "' isn't in the" +
                    "taxonomy"); }
                ex.printStackTrace();
            }
        }
    	
        return result;
    }


    private Hashtable<Category, Double> calculateNameGoodness(HTaxonomy t) {
        Hashtable<Category, Double> result = new Hashtable<Category, Double>();
        
        Iterator <Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category category = ite.next();
            try {
            	URI nameURI = URI.create(category.getName());
            	
            	String name = nameURI.getFragment();
            	if (name == null) { /* The item has not a fragment */
            		String path = nameURI.getPath();
            		if (path != null) {
            			int lastIndex = path.lastIndexOf("/");
            			
            			if (lastIndex > -1 && lastIndex + 1 == path.length()) {
            				path = path.substring(0, lastIndex);
            				lastIndex = path.lastIndexOf("/");
            			}
            			
                		if (lastIndex > -1) {
                			name = path.substring(lastIndex + 1);
                		} else {
                			name = path;
                		}
            		} else {
            			name = category.getName();
            		}
            	}
            	
                /* This pattern matches with every string that begins with '-' or '_' */
                boolean beginsWithUnderscoreMinus = name.matches("[-_\\.:].*");
                int underScoreOccurence = StringFacility.indexOf(name, "_").size();
                int minusOccurrence = StringFacility.indexOf(name, "-").size();
                int dotOccurrence = StringFacility.indexOf(name, ".").size();
                int colonOccurrence = StringFacility.indexOf(name, ":").size();
                int totalLack = underScoreOccurence + minusOccurrence + dotOccurrence + 
                colonOccurrence - (beginsWithUnderscoreMinus ? 1 : 0);

                /* This pattern matches with every string that is composed without non-capital letter */
                //boolean onlyCapitalLetters = name.matches("[^a-z]*");
                /* This pattern matches with every string that contains (in the middle) at least one
                 * capital letter (this assumption is valid if and only if the string isn't composed
                 * by capital letters only). The capital composition approach is used in the goodness
                 * if and only if 'totalLack' is zero */
                /*
                boolean capitalComposition = onlyCapitalLetters ? false : name.matches("..*[A-Z].*");
                
                double goodnessTmp = 
                        1.0 
                        - 
                        (this.compoundRatio * (totalLack + (capitalComposition && totalLack == 0 ? 1 : 0)));
                */
                
                double goodnessTmp = 1.0 - (
                		this.compoundRatio * (totalLack + humaniseCamelCase(name).split(" ").length - 1));
                double goodness = (goodnessTmp < 0.0 ? 0.0 : Math.min(1.0, goodnessTmp));
                result.put(category, new Double(goodness));
                t.addInfo(category, "NaturalCategoryNameGoodness", Double.toString(goodness));
                
                if (debug) System.out.println("\tsimplicity for '" + name + "':" + goodness);
            } catch (NoCategoryException ex) {
                if (debug) { System.out.println("[NaturalCategory: findNaturalCategories] ERROR - The '" 
                        + category.getName() + "' isn't in the" +
                    "taxonomy");}
                ex.printStackTrace();
            }
        }
                
        return result;
    }

    /* I work out the natural category value for all the categories */
    private HTaxonomy findNaturalCategories(
            HTaxonomy t,
            Hashtable<Category, Double> basicLevels, 
            Hashtable<Category, Double> nameGoodness) {
        
        Hashtable<Category, Double> candidates = new Hashtable<Category, Double>();
        
        /* I calculate the final value for each category */
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category category = ite.next();
            try {
                double basicLevel = basicLevels.get(category).doubleValue();
                double goodness = nameGoodness.get(category).doubleValue();
                double value = MathFacility.normalize(
                        1.0,
                        basicLevel * this.levelWeight + goodness * this.nameWeight,
                        this.levelWeight + this.nameWeight);
                
                /* If the category is the root category or a leaf category the value is setted to
                 * zero because these category can't be natural cateogory */
                if (t.getRoot() == category || t.getDirectSubCategories(category).isEmpty()) {
                    value = 0.0;
                }

                t.addInfo(category, "NaturalCategoryValue", Double.toString(value));
                t.addInfo(category, "NaturalCategoryIs", "no");

                if (
                        value > this.threshold                  && 
                        !t.getDirectSubCategories(category).isEmpty() &&
                        category != t.getRoot()
                   ) {
                    candidates.put(category, new Double(value));
                }
                
            } catch (NoCategoryException ex) {
                if (debug) { System.out.println("[NaturalCategory: findNaturalCategories] ERROR - The '" 
                        + category.getName() + "' isn't in the" +
                    "taxonomy"); }
                ex.printStackTrace();
            }
        }
        
        /* I can say that a category is natural if and only if:
         *   - it is in the candidate map;
         *   - any descendant category hasn't been already selected as natural;
         *   - any ancestor category hasn't been already selected as natural. */
        while (!candidates.isEmpty()) {
            Category candidate = (Category) MapFacility.getKeyWithMaxDoubleValue((Map) candidates);
            
            try {
                t.addInfo(candidate, "NaturalCategoryIs", "yes");
                if (debug) { System.out.println("\tNatural Category: " + candidate.getName() + " [value = " +
                            candidates.get(candidate) + "]"); }
                
                candidates.remove(candidate);
                
                /* I remove from the candidates map all the category ancestors and descendants */
                Set<Category> descendants = t.getAllSubCategories(candidate);
                Set<Category> ancestors = t.getAllSuperCategories(candidate);
                MapFacility.removeAllKeysFromMap(descendants, candidates);
                MapFacility.removeAllKeysFromMap(ancestors, candidates);
                
            } catch (NoCategoryException ex) {
                if (debug) { System.out.println("[NaturalCategory: findNaturalCategories] ERROR - The '" 
                        + candidate.getName() + "' isn't in the" +
                    "taxonomy"); }
                ex.printStackTrace();
            }
        }
        
        return t;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String humaniseCamelCase(String word) {
        Pattern pattern = Pattern.compile("([A-Z]|[a-z])[a-z]*");

        Vector<String> tokens = new Vector<String>();
        Matcher matcher = pattern.matcher(word);
        String acronym = "";
        while(matcher.find()) {
            String found = matcher.group();
            if(found.matches("^[A-Z]$")) {
                acronym += found;
            } else {
                if(acronym.length() > 0) {
                    tokens.add(acronym);
                    acronym  = "";
                }
                tokens.add(found.toLowerCase());
            }
        }
        if(acronym.length() > 0) {
            tokens.add(acronym);
        }
        if (tokens.size() > 0) {
            String humanisedString = "";
            for (String s : tokens) {
                humanisedString += s + " ";
            }
            return humanisedString.substring(0,humanisedString.length() - 1);
        }

        return word;
    }

}
