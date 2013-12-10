package it.essepuntato.semanticweb.kce.algorithm;

import it.essepuntato.taxonomy.HTaxonomy;
import java.util.Map;

/**
 * @author Silvio Peroni
 */
public interface IAlgorithm {
    /**
     * This method returns a map. Use the keyword "taxonomy" to retrieve the HTaxonomy
     * with all the values for the algorithm executed.
     * 
     * @param ht the taxonomy to evaluate.
     * @param params params that can be used by this algorithm.
     * @return a map containing the result of the algorithm execution.
     */
    public Map<String,Object> evaluate(HTaxonomy ht, Map<String,String> params);
    
    public String getName();
    public void setName(String name);
}
