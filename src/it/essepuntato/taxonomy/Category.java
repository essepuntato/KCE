package it.essepuntato.taxonomy;

import it.essepuntato.taxonomy.patterns.Visitor;

/**
 * @author Silvio Peroni
 */
public class Category {
    private String name = "";
    
    public Category(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object c) {
        return ((Category)c).getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
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
