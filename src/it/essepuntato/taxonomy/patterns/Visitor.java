package it.essepuntato.taxonomy.patterns;

import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.Instance;
import it.essepuntato.taxonomy.Property;

/**
 * @author Silvio Peroni
 */
public interface Visitor {
    public Object visit(Category c);
    public Object visit(Property p);
    public Object visit(Instance i);
    public Object visit(HTaxonomy t);
}
