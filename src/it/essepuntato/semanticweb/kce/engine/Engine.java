package it.essepuntato.semanticweb.kce.engine;

import it.essepuntato.facility.math.MathFacility;
import it.essepuntato.semanticweb.kce.algorithm.Coverage;
import it.essepuntato.semanticweb.kce.algorithm.Density;
import it.essepuntato.semanticweb.kce.algorithm.IAlgorithm;
import it.essepuntato.semanticweb.kce.algorithm.NaturalCategory;
import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silvio Peroni
 */
public class Engine {
	
	private boolean maxRetrieved = false;
	private double maxBasicLevel = 0.0;
	private double maxNameSimplicity = 0.0;
	private double maxGlobalDensity = 0.0;
	private double maxLocalDensity = 0.0;
	private double maxCoverage = 0.0;
    
    private HTaxonomy ht = null;
    private List<IAlgorithm> algorithms = null;
    private List<Map<String,String>> parameters = null;
    private boolean sequence = true;
    
    public final static String rootName = "http://www.essepuntato.it/OntoAlgorithm#ESSEPUNTATO";
    
    public Engine(
            HTaxonomy ht, 
            List<IAlgorithm> algorithms,
            List<Map<String,String>> parameters) {
        this.ht = ht;
        this.algorithms = algorithms;
        this.parameters = parameters;
    }
    
    public Engine(
            HTaxonomy ht, 
            List<IAlgorithm> algorithms) {
        this.ht = ht;
        this.algorithms = algorithms;
        setDefaultParameters();
    }
    
    public Engine(HTaxonomy ht) {
        this.ht = ht;
        this.algorithms = new ArrayList<IAlgorithm>();
        setDefaultAlgorithms();
        setDefaultParameters();
    }
    
    private void setDefaultAlgorithms() {
		algorithms.add(new Density("density"));
		algorithms.add(new NaturalCategory("natural"));
		algorithms.add(new Coverage("coverage"));
	}

	/* NB: this method modifies what previously has been specified in the parameters */
    public void setNumberOfKeyConceptsToExtract(int n) {
    	if (n > 0 && n < Integer.MAX_VALUE) {
    		Map<String,String> coverage = this.parameters.get(2);
    		if (coverage != null) {
    			coverage.put("n", new Integer(n).toString());
    			coverage.put("k", new Integer(n - 2).toString());
    		}
    	}
    }
    
    public void setSequence(boolean bool) {
        this.sequence = bool;
    }
    
    public HTaxonomy run() {
    	maxRetrieved = false;
        Hashtable<String,Object> result = new Hashtable<String,Object>();
        
        Iterator<IAlgorithm> algorithmIte = this.algorithms.iterator();
        Iterator<Map<String,String>> paramenterIte = this.parameters.iterator();
        
        /* I execute all the algorithm */
        HTaxonomy newHt = this.ht;
        
        while (algorithmIte.hasNext()) {
            IAlgorithm algo = algorithmIte.next();
            String algoName = algo.getName();
            Map<String,String> parameters = paramenterIte.next();
            
            Map<String,Object> algorithmResult = algo.evaluate(newHt, parameters);
            result.put(algoName + ".taxonomy", algorithmResult.get("taxonomy"));
            
            if (this.sequence) {
                newHt = (HTaxonomy) algorithmResult.get("taxonomy");
            }
        }
        
        return newHt;
    }
    
    private void setDefaultParameters() {
    	parameters = new ArrayList<Map<String,String>>();
		
		Map<String,String> densityParameters = new HashMap<String, String>();
		densityParameters.put("globalDensityWeight", "1");
		densityParameters.put("localDensityWeight", "4");
//		densityParameters.put("categoryWeight", "8");
		densityParameters.put("directSubclassesWeight", "8");
		densityParameters.put("indirectSubclassesWeight", "0");
		densityParameters.put("propertyWeight", "1");
		densityParameters.put("instanceWeight", "1");
		densityParameters.put("range", "2");
		densityParameters.put("ratio", "0.1");
		densityParameters.put("threshold", "0.5");
		densityParameters.put("globalDensityWeightLocally", "0.5");
		parameters.add(densityParameters);
		
		Map<String,String> naturalParameters = new HashMap<String, String>();
		naturalParameters.put("levelWeight", "2");
		naturalParameters.put("nameWeight", "1");
		naturalParameters.put("threshold", "0.5");
		naturalParameters.put("compoundRatio", "0.3");
		parameters.add(naturalParameters);
		
		Map<String,String> coverageParameters = new HashMap<String, String>();
		coverageParameters.put("n", "20");
		coverageParameters.put("k", "18");
		coverageParameters.put("coverageContributionWeight", "3");
		coverageParameters.put("criteriaWeight", "2");
		coverageParameters.put("useGlobalLocalValues", "yes");
		coverageParameters.put("naturalWeight", "30");
		coverageParameters.put("densityWeight", "40");
		coverageParameters.put("basicLevelWeight", "0.146");
		coverageParameters.put("nameSimplicityWeight", "0.1");
		coverageParameters.put("coverageWeight", "0.102");
		coverageParameters.put("popularityWeight", "0");
		coverageParameters.put("localDensityWeight", "0.08");
		coverageParameters.put("localPopularityWeight", "0");
		coverageParameters.put("globalDensityWeight", "0.572");
		coverageParameters.put("globalPopularityWeight", "0");
		coverageParameters.put("coverageDistribuctionThreshold", "3000");
		parameters.add(coverageParameters);
    }
    
    private void findTaxomonylimits(HTaxonomy hTaxonomy) {
		for (Category aClass : hTaxonomy.getAllCategories()) {
			findMax(aClass, hTaxonomy);
		}
	}
	
	private void findMax(Category category, HTaxonomy ht) {
		try {
			Double basicLevel = new Double(ht.getInfo(category, "NaturalCategoryBasicLevel"));
			if (basicLevel > maxBasicLevel) {
				maxBasicLevel = basicLevel;
			}
			
			Double nameSimplicity = new Double(ht.getInfo(category, "NaturalCategoryNameGoodness"));
			if (nameSimplicity > maxNameSimplicity) {
				maxNameSimplicity = nameSimplicity;
			}
			
			Double globalDensity = new Double(ht.getInfo(category, "DensityGlobal"));
			if (globalDensity > maxGlobalDensity) {
				maxGlobalDensity = globalDensity;
			}
			
			Double localDensity = new Double(ht.getInfo(category, "DensityLocal"));
			if (localDensity > maxLocalDensity) {
				maxLocalDensity = localDensity;
			}
			
			Double coverage = new Double(ht.getInfo(category, "coverage"));
			if (coverage > maxCoverage) {
				maxCoverage = coverage;
			}
		} catch (NumberFormatException e) {
			// DO NOTHING
		} catch (NoCategoryException e) {
			// DO NOTHING
		}
	}
	
	public String getDescription(String name) {
		if (ht != null) {
			try {
				Category category = ht.getCategoryByName(name);
				return getDescription(category);
			} catch (NoCategoryException e) {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public String getDescription(Category category) {
		if (!maxRetrieved && ht != null) {
			findTaxomonylimits(ht);
			maxRetrieved = true;
		}
		
		String result = "";
		
		try {
			Double basicLevel = new Double(ht.getInfo(category, "NaturalCategoryBasicLevel"));
			result += "Basic level: " + 
				getDescriptionAccordingToMax(ratioToMax(basicLevel, maxBasicLevel), 1.0);
			
			Double nameSimplicity = new Double(ht.getInfo(category, "NaturalCategoryNameGoodness"));
			result += "\nName simplicity: " + 
				getDescriptionAccordingToMax(
						ratioToMax(nameSimplicity, maxNameSimplicity), 1.0);
			
			Double globalDensity = new Double(ht.getInfo(category, "DensityGlobal"));
			result += "\nGlobal density: " + 
				getDescriptionAccordingToMax(
					ratioToMax(globalDensity, maxGlobalDensity), 1.0);
			
			Double localDensity = new Double(ht.getInfo(category, "DensityLocal"));
			result += "\nLocal density: " + 
				getDescriptionAccordingToMax(
						ratioToMax(localDensity, maxLocalDensity), 1.0);
			
			Double coverage = new Double(ht.getInfo(category, "coverage"));
			result += "\nCoverage: " + 
				getDescriptionAccordingToMax(
						ratioToMax(coverage, maxCoverage), 1.0);
			
		} catch (Exception e) {
			// DO NOTHING
		}
		
		return result;
	}
	
	private double ratioToMax(double number, double max) {
		return 
        	(max == 0.0 ? 
        			0.0 :
                    MathFacility.normalize(
                        1.0, 
                        number, 
                        max)
            );
	}
	
	private String getDescriptionAccordingToMax(double ratio, double max) {
		double step = max / 10.0;
		if (ratio == 0.0) {
			return "none [0]";
		} else if (0.0 < ratio && ratio < step) {
			return "unsatisfactory [1]";
		} else if (step <= ratio && ratio < 2 * step) {
			return "very poor [2]";
		} else if (2 * step <= ratio && ratio < 3 * step) {
			return "poor [3]";
		} else if (3 * step <= ratio && ratio < 4 * step) {
			return "quite sufficient [4]";
		} else if (4 * step <= ratio && ratio < 5 * step) {
			return "fair [5]";
		} else if (5 * step <= ratio && ratio < 6 * step) {
			return "fine [6]";
		} else if (6 * step <= ratio && ratio < 7 * step) {
			return "good [7]";
		} else if (7 * step <= ratio && ratio < 8 * step) {
			return "strong [8]";
		} else if (8 * step <= ratio && ratio < 9 * step) {
			return "very strong [9]";
		} else if (9 * step <= ratio && ratio < 10 * step) {
			return "excellent [10]";
		} else {
			return "top [10+]";
		}
	}
	
	public Set<String> getKeyConcepts() {
		Set<String> result = new HashSet<String>();
		
		if (ht != null) {
			Iterator<Category> ite = ht.getAllCategories().iterator();
			while (ite.hasNext()) {
				Category currentCategory = ite.next();
				if (!currentCategory.getName().equals(rootName)) {
					try {
						String coverageIs = ht.getInfo(currentCategory, "CoverageIs");
						if (coverageIs != null && coverageIs.equals("yes")) {
							result.add(currentCategory.getName());
						}
					} catch (NoCategoryException e) {
						System.out.println("[KeyConceptsHandler: getKeyConcepts] ERROR - The category '" +
	                            currentCategory.getName() + "' isn't in the taxonomy");
						e.printStackTrace();
					}
				}
			}
		}
		
		return result;
	}
}
