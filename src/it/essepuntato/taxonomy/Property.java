package it.essepuntato.taxonomy;

import java.util.Set;

import it.essepuntato.taxonomy.patterns.Visitor;

/**
 * @author Silvio Peroni
 */
public class Property {
    private String name = "";
    
    public Property(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object p) {
        return ((Property)p).getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
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
