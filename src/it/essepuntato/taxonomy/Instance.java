package it.essepuntato.taxonomy;

import it.essepuntato.taxonomy.patterns.Visitor;

/**
 * @author Silvio Peroni
 */
public class Instance {
    private String name = "";
    
    public Instance(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object i) {
        return ((Instance)i).getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    public Object accept(Visitor v) {
        return v.visit(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
