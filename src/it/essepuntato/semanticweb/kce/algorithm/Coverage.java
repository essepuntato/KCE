package it.essepuntato.semanticweb.kce.algorithm;

import it.essepuntato.facility.collection.CollectionFacility;
import it.essepuntato.facility.list.ListFacility;
import it.essepuntato.facility.map.MapFacility;
import it.essepuntato.facility.math.MathFacility;
import it.essepuntato.facility.set.SetFacility;
import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silvio Peroni
 */
public class Coverage implements IAlgorithm {
    
    private int n = 20;
    private int k = 15;
    private boolean useGlobalLocalValues = true;
    private double coverageContributionWeight = 1.0;
    private double criteriaWeight = 1.0;
    private double naturalWeight = 1.0;
    private double densityWeight = 1.0;
    private double popularityWeight = 1.0;
    private double localDensityWeight = 1.0;
    private double localPopularityWeight = 1.0;
    private double globalDensityWeight = 1.0;
    private double globalPopularityWeight = 1.0;
    private double nameGoodnessWeight = 1.0;
    private double basicLevelWeight = 1.0;
    private String name = "";
	private double coverageWeight = 1.0;
	private boolean debug = false;
	private int coverageDistribuctionThreshold = 3000;
    
    public Coverage(String string) {
        this.name = string;
    }

    public Map<String, Object> evaluate(HTaxonomy ht, Map<String, String> params) {
        System.out.println("\n-- Coverage Algorithm: BEGIN");
        //HTaxonomy t = ht.clone();
        HTaxonomy t = ht;
        
        this.n = new Integer(params.get("n")).intValue();
        this.k = new Integer(params.get("k")).intValue();
        this.coverageContributionWeight = new Double(params.get("coverageContributionWeight")).doubleValue();
        this.criteriaWeight = new Double(params.get("criteriaWeight")).doubleValue();
        this.useGlobalLocalValues =
                new Boolean((params.get("useGlobalLocalValues").equals("yes") ? true : false)).booleanValue();
        this.naturalWeight = new Double(params.get("naturalWeight")).doubleValue();
        this.basicLevelWeight = new Double(params.get("basicLevelWeight")).doubleValue();
        this.nameGoodnessWeight = new Double(params.get("nameSimplicityWeight")).doubleValue();
        this.densityWeight = new Double(params.get("densityWeight")).doubleValue();
        this.popularityWeight = new Double(params.get("popularityWeight")).doubleValue();
        this.localDensityWeight = new Double(params.get("localDensityWeight")).doubleValue();
        this.localPopularityWeight = new Double(params.get("localPopularityWeight")).doubleValue();
        this.globalDensityWeight = new Double(params.get("globalDensityWeight")).doubleValue();
        this.globalPopularityWeight = new Double(params.get("globalPopularityWeight")).doubleValue();
        this.coverageWeight  = new Double(params.get("coverageWeight")).doubleValue();
        this.coverageDistribuctionThreshold  = 
        	new Integer(params.get("coverageDistribuctionThreshold")).intValue();
        
        if (debug)System.out.println("Coverage Algorithm:");
        if (debug)System.out.println("\tn = " + this.n);
        if (debug)System.out.println("\tk = " + this.k);
        if (debug)System.out.println("\tcoverageContributionWeight = " + this.coverageContributionWeight);
        if (debug)System.out.println("\tcriteriaWeight = " + this.criteriaWeight);
        if (debug) System.out.println("\tnaturalWeight = " + this.naturalWeight);
        if (debug) System.out.println("\tbasicLevelWeight = " + this.basicLevelWeight);
        if (debug) System.out.println("\tnameGoodnessWeight = " + this.nameGoodnessWeight);
        if (debug) System.out.println("\tdensityWeight = " + this.densityWeight);
        if (debug) System.out.println("\tpopularityWeight = " + this.popularityWeight);
        if (debug) System.out.println("\tlocalDensityWeight = " + this.localDensityWeight);
        if (debug) System.out.println("\tlocalPopularityWeight = " + this.localPopularityWeight);
        if (debug) System.out.println("\tglobalDensityWeight = " + this.globalDensityWeight);
        if (debug) System.out.println("\tglobalPopularityWeight = " + this.globalPopularityWeight);
        if (debug) System.out.println("\tcoverageWeight = " + this.coverageWeight);
        if (debug) System.out.println("\tcoverageDistribuctionThreshold = " + this.coverageDistribuctionThreshold);
        
        if (debug) System.out.println("Coverage Algorithm: retrieving the density");
        Map<Category,Double> density = this.retrieveDensity(t);
        if (debug) System.out.println("Coverage Algorithm: retrieving the local density");
        Map<Category,Double> localDensity = this.retrieveLocalDensity(t);
        if (debug) System.out.println("Coverage Algorithm: retrieving the global density");
        Map<Category,Double> globalDensity = this.retrieveGlobalDensity(t);
        if (debug) System.out.println("Coverage Algorithm: retrieving the natural category");
        Map<Category,Double> naturalCategory = this.retrieveNaturalCategory(t);
        if (debug) System.out.println("Coverage Algorithm: retrieving the basic level");
        Map<Category,Double> basicLevel = this.retrieveBasicLevel(t);
        if (debug) System.out.println("Coverage Algorithm: retrieving the name goodness");
        Map<Category,Double> nameGoodness = this.retrieveNameGoodness(t);
        
        if (debug) System.out.println("Coverage Algorithm: calculating the covered category");
        Map<Category,Set<Category>> covered = this.calculateCovered(t);
        
        if (debug) System.out.println("Coverage Algorithm: calculating the coverage values");
        Map<Category,Double> coverage = this.calculateCoverage(t, covered);
        
        if (debug) System.out.println("Coverage Algorithm: calculating the criteria values");
        Map<Category,Double> criteria = this.calculateCriteriaValues(
                t, 
                density, localDensity, globalDensity, 
                naturalCategory, basicLevel, nameGoodness, coverage);
        
        if (debug) System.out.println("Coverage Algorithm: calculating the best coverage");
        Set<Category> bestCoverage = this.findBestCoverage(t, criteria, coverage, covered);
        
        Hashtable<String, Object> result = new Hashtable<String,Object>();
        result.put("taxonomy", t);
        
        System.out.println("-- Coverage Algorithm: END\n");
        return result;
    }

    private Map<Category, Double> calculateCombinedValues(
            HTaxonomy t, 
            Set<Category> setA, 
            Set<Category> setB, 
            Map<Category, Double> criteria,
            Map<Category, Set<Category>> covered) {
        Map<Category,Double> result = new Hashtable<Category,Double>();
        
        Map<Category, Double> contribution = this.calculateContributionValues(t, setA, setB, covered);
        
        double max = MathFacility.maxDouble(new ArrayList<Double>(contribution.values()));
        Iterator<Category> ite = contribution.keySet().iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            
            double weightedContribution = 
                    (max == 0.0 ? 0.0 : MathFacility.normalize(1.0, contribution.get(c), max));
            //if (debug) System.out.println("The weighted contribution of " + c.getName() + ":" + weightedContribution);
            
            double value = 
                    (this.criteriaWeight * criteria.get(c)) +
                    (this.coverageContributionWeight * weightedContribution);
            double realValue = MathFacility.normalize(
                        1.0, 
                        value, 
                        new Double(this.criteriaWeight + this.coverageContributionWeight).doubleValue());
            //if (debug) System.out.println("Combined value of '" + c.getName() + "' is: " + realValue);
            result.put(c, realValue);
        }
        
        return result;
    }

    private Map<Category, Double> calculateContributionValues(
            HTaxonomy t, Set<Category> setA, Set<Category> setB, Map<Category,Set<Category>> covered) {
        Map<Category,Double> result = new Hashtable<Category,Double>();
        
        Map<Category,Integer> intResult = new Hashtable<Category, Integer>();
        Set<Category> current = new HashSet<Category>();
        current.addAll(setA);
        current.addAll(setB);
        
        Iterator<Category> ite = current.iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            Set<Category> cCovered = (Set<Category>) CollectionFacility.copy(covered.get(c));
            Set<Category> inCoverage = (Set<Category>) SetFacility.intersect(cCovered, current);
            
            Iterator<Category> inCoverageIterator = inCoverage.iterator();
            while (inCoverageIterator.hasNext()) {
                Category cC = inCoverageIterator.next();
                if (c != cC) {
                    cCovered.removeAll(covered.get(cC));
                }
            }
            
            intResult.put(c, cCovered.size());
        }
        
        double sum = new Double(t.getAllCategories().size()).doubleValue();
        ite = intResult.keySet().iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            double newValue = 
                    MathFacility.normalize(1.0, new Double(intResult.get(c)).doubleValue(), sum);
            //if (debug) System.out.println("Contribution of '" + c.getName() + "' is: " + newValue);
            result.put(c, new Double(newValue));
        }
        
        return result;
    }

    private Map<Category, Double> calculateCoverage(HTaxonomy t, Map<Category, Set<Category>> covered) {
        Map<Category,Double> result = new Hashtable<Category,Double>();
        
        Iterator<Category> ite = covered.keySet().iterator();
        Double categories = new Double(t.getAllCategories().size());
        while (ite.hasNext()) {
            Category c = ite.next();
            Double finalCoverage = new Double(new Double(covered.get(c).size()) / categories);
            try {
				t.addInfo(c, "coverage", finalCoverage.toString());
			} catch (NoCategoryException e) {
				System.err.println("[Coverage: calculateCoverage] ERROR - The category '" + 
                        c.getName() + "' isn't in the taxonomy.");
                e.printStackTrace();
			}
            result.put(c, finalCoverage);
        }
        
        return result;
    }

    private Map<Category, Set<Category>> calculateCovered(HTaxonomy t) {
        Map<Category,Set<Category>> result = new Hashtable<Category,Set<Category>>();
        
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            try {
            	Set<Category> set = new HashSet<Category>();
            	set.add(c);
            	set.addAll(t.getDirectSubCategories(c));
            	set.addAll(t.getAllSuperCategories(c));

                result.put(c, set);
            } catch (NoCategoryException ex) {
                System.err.println("[Coverage: calculateCovered] ERROR - The category '" + 
                        c.getName() + "' isn't in the taxonomy.");
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private Map<Category, Double> calculateCriteriaValues(
            HTaxonomy t, 
            Map<Category, Double> density,
            Map<Category, Double> localDensity,
            Map<Category, Double> globalDensity,
            Map<Category, Double> naturalCategory,
            Map<Category, Double> basicLevel,
            Map<Category, Double> nameGoodness,
            Map<Category, Double> coverage) {
        Map<Category,Double> result = new Hashtable<Category,Double>();
        
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            Double value = null;
            Double den = null;
            
            if (this.useGlobalLocalValues) {
                value = new Double(
                        this.globalDensityWeight * globalDensity.get(c) +
                        this.localDensityWeight * localDensity.get(c) +
                        this.basicLevelWeight * basicLevel.get(c) +
                        this.nameGoodnessWeight * nameGoodness.get(c) +
                        this.coverageWeight * coverage.get(c)
                );
                
                den = new Double(
                        this.globalDensityWeight + this.localDensityWeight + 
                        this.basicLevelWeight + this.nameGoodnessWeight + this.coverageWeight);
            }
            else {
                value = new Double(
                        this.densityWeight * density.get(c) +
                        this.naturalWeight * naturalCategory.get(c) +
                        this.coverageWeight * coverage.get(c)
                );
                
                den = new Double(
                        this.globalDensityWeight + 
                        this.naturalWeight +
                        this.coverageWeight);
            }
            
            double curCriteria = MathFacility.normalize(1.0, value.doubleValue(), den.doubleValue());
            try {
				t.addInfo(c, "criteria", new Double(curCriteria).toString());
			} catch (NoCategoryException e) {
				if (debug) { System.out.println("[Coverage: findBestCoverage] ERROR - The category '" +
                        c.getName() + "' isn't in the taxonomy"); }
				e.printStackTrace();
			}
            result.put(c, curCriteria);
        }
        
        /* Normalization considering the max value calculated */
        Double max = MathFacility.maxDouble(new ArrayList<Double>(result.values()));
        ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            Double value = 
            		(max.doubleValue() == 0.0 ? 
            				0.0 :
            				new Double(MathFacility.normalize(1.0, result.get(c).doubleValue(), max.doubleValue())));
            if (debug) { System.out.println("\tcriteria for the class '" + c.getName() + "' is " + value + " (normalized" +
                    " with a max value of " + max + ")"); }
            result.put(c, value);
        }
        
        return result;
    }

    private Set<Category> findBestCoverage(
            HTaxonomy t, 
            Map<Category, Double> criteria, 
            Map<Category, Double> coverage, 
            Map<Category, Set<Category>> covered) {
        Set<Category> result = new HashSet<Category>();
        
        Map<Category, Double> cCriteria = MapFacility.copy(criteria);
        
        if (t.getAllCategories().size() > coverageDistribuctionThreshold) { /* We do not use the
        coverage maximization process if we have more than 'coverageDistribuctionThreshold' classes
        in the taxonomy */
        	k = n;
        }
        
        boolean passThreshold = false;
        if (debug) System.out.println("# Finding the best " + this.k + " categories for the coverage");
        while (result.size() < this.k && !cCriteria.isEmpty() && !passThreshold) {
            List<Category> list = MapFacility.getKeysWithMaxDoubleValue((Map)cCriteria);
            if (result.size() + list.size() <= this.n) {
            	MapFacility.removeAllKeysFromMap(list, cCriteria);
                result.addAll(list);
                
                Iterator<Category> ite = list.iterator();
                while (debug && ite.hasNext()) {
                    if (debug) System.out.println("\tadded a new category in the coverage set: " + ite.next().getName());
                }
            }
            else {
            	passThreshold = true; 
            }
        }
        int newK = result.size();
        if (debug) System.out.println("# " + newK + " categories found");
        
        if (debug) System.out.println("# Finding the remaining " + (this.n - newK) + " categories for the coverage");
        Set<Category> remaining = new HashSet<Category>();
        while (remaining.size() < (this.n - newK) && !cCriteria.isEmpty()) {
            List<Category> list = MapFacility.getKeysWithMaxDoubleValue((Map)cCriteria);
            
            MapFacility.removeAllKeysFromMap(list, cCriteria);
            remaining.addAll(list);
            
            Iterator<Category> ite = list.iterator();
            while (debug && ite.hasNext()) {
                if (debug) System.out.println("\tadded a provisory category in the coverage set: " + ite.next().getName());
            }
        }
        int newN = remaining.size() + newK;
        if (debug) System.out.println("# " + (newN - newK) + " provisory categories found");
        
        boolean found = false;
        while (!found && (newN - newK) > 0) {
            Map<Category,Double> combinedValues = 
                    this.calculateCombinedValues(t, result, remaining, criteria, covered);
            double average = MathFacility.averageDouble(new ArrayList(combinedValues.values()));
            
            Map<Category,Double> contributionValues = 
                    this.calculateContributionValues(t, result, remaining, covered);
            double cAverage = MathFacility.averageDouble(new ArrayList(contributionValues.values()));
            
            if (debug) System.out.println("Current average: " + average);
            if (debug) System.out.println("Current contribution average: " + cAverage);
            
            MapFacility.removeAllKeysFromMap(result, combinedValues);
            Category worst = (Category) MapFacility.getKeyWithMinDoubleValue((Map)combinedValues);
            
            Map<Category, Double> excluded = MapFacility.copy(covered);
            MapFacility.removeAllKeysFromMap(result, excluded);
            MapFacility.removeAllKeysFromMap(remaining, excluded);
            excluded.remove(t.getRoot());
            boolean switched = false;
            
            List<Category> list = this.sortByName(new ArrayList(excluded.keySet()));
            Iterator<Category> ite = list.iterator();
            if (!ite.hasNext())
            	found = true;
            while (ite.hasNext() && !switched) {
                Category c = ite.next();
                Set<Category> cRemaining = (Set<Category>) CollectionFacility.copy(remaining);
                cRemaining.remove(worst);
                cRemaining.add(c);
                Map<Category,Double> newCombinedValues = 
                        this.calculateCombinedValues(t, result, cRemaining, criteria, covered);
                double newAverage = MathFacility.averageDouble(new ArrayList(newCombinedValues.values()));
                Map<Category,Double> newContributionValues = 
                        this.calculateContributionValues(t, result, cRemaining, covered);
                double newCAverage = MathFacility.averageDouble(new ArrayList(newContributionValues.values()));
                
                if (newAverage > average && newCAverage >= cAverage) {
                    if (debug) { System.out.println("The class '" + worst.getName() + "' is switched with the class '" +
                            c.getName() + "'"); }
                    remaining = cRemaining;
                    switched = true;
                }
                else if (!ite.hasNext()) {
                    found = true;
                }
            }
        }
        
        result.addAll(remaining);
        
        //setting the info
        Iterator<Category> resultIterator = result.iterator();
        if (debug) System.out.println("# Best coverage:");
        while (resultIterator.hasNext()) {
            Category c = resultIterator.next();
            try {
                t.addInfo(c, "CoverageIs", "yes");
                if (debug) System.out.println("\t- " + c.getName());
            } catch (NoCategoryException ex) {
                if (debug) { System.out.println("[Coverage: findBestCoverage] ERROR - The category '" +
                            c.getName() + "' isn't in the taxonomy"); }
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private Map<Category, Double> retrieveDensity(HTaxonomy t) {
        return this.retrieveValues(t, "DensityFinal");
    }

    private Map<Category, Double> retrieveGlobalDensity(HTaxonomy t) {
        return this.retrieveValues(t, "DensityGlobal");
    }

    private Map<Category, Double> retrieveGlobalPopularity(HTaxonomy t) {
        return this.retrieveValues(t, "PopularityGlobal");
    }

    private Map<Category, Double> retrieveLocalDensity(HTaxonomy t) {
        return this.retrieveValues(t, "DensityLocal");
    }

    private Map<Category, Double> retrieveLocalPopularity(HTaxonomy t) {
        return this.retrieveValues(t, "PopularityLocal");
    }

    private Map<Category, Double> retrieveNaturalCategory(HTaxonomy t) {
        return this.retrieveValues(t, "NaturalCategoryValue");
    }
    
    private Map<Category, Double> retrieveNameGoodness(HTaxonomy t) {
        return this.retrieveValues(t, "NaturalCategoryNameGoodness");
    }
    
    private Map<Category, Double> retrieveBasicLevel(HTaxonomy t) {
        return this.retrieveValues(t, "NaturalCategoryBasicLevel");
    }

    private Map<Category, Double> retrievePopularity(HTaxonomy t) {
        return this.retrieveValues(t, "PopularityFinal");
    }

    private Map<Category, Double> retrieveValues(HTaxonomy t, String value) {
        Map<Category, Double> result = new Hashtable<Category, Double>();
        
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext()) {
            Category category = ite.next();
            try {
                String tmpValue = t.getInfo(category, value);
                if (tmpValue != null) {
                    result.put(category, new Double(tmpValue));
                }
                else {
                    System.err.println("[Coverage: retrieveValues] WARNING - The category '" + 
                        category.getName() + "' has not the value '" + value + "' as its information.");
                }
            } catch (NoCategoryException ex) {
                System.err.println("[Coverage: retrieveValues] ERROR - The category '" + 
                        category.getName() + "' isn't in the taxonomy.");
                ex.printStackTrace();
            }
        }
        
        return result;
    }

    private List<Category> sortByName(ArrayList<Category> arrayList) {
        List<Category> result = new ArrayList<Category>();
        
        List<String> tmp = new ArrayList<String>();
        Hashtable<String,Category> h = new Hashtable<String, Category>();
        Iterator<Category> iteC = arrayList.iterator();
        while (iteC.hasNext()) {
            Category c = iteC.next();
            tmp.add(c.getName());
            h.put(c.getName(), c);
        }
        
        Collections.sort(tmp);
        
        Iterator<String> iteS = tmp.iterator();
        while (iteS.hasNext()) {
            result.add(h.get(iteS.next()));
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
