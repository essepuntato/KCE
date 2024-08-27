package it.essepuntato.taxonomy;

import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.taxonomy.exceptions.NoInstanceException;
import it.essepuntato.taxonomy.exceptions.NoPropertyException;
import it.essepuntato.taxonomy.exceptions.RootException;
import it.essepuntato.taxonomy.patterns.Visitor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silvio Peroni
 */
public class HTaxonomy implements Taxonomy{
    private Set<Category> categories = new HashSet<Category>();
    private Set<Property> properties = new HashSet<Property>();
    private Set<Instance> instances = new HashSet<Instance>();
    
    private Hashtable<String,String> iTaxonomy = new Hashtable<String, String>();
    private Hashtable<Category,Hashtable<String,String>> iClasses = 
            new Hashtable<Category, Hashtable<String, String>>();
    private Hashtable<Property,Hashtable<String,String>> iProperties = 
            new Hashtable<Property, Hashtable<String, String>>();
    private Hashtable<Instance,Hashtable<String,String>> iInstances = 
            new Hashtable<Instance, Hashtable<String, String>>();
    
    private Hashtable<Category,Set<Category>> superClasses = 
            new Hashtable<Category, Set<Category>>();
    private Hashtable<Property,Set<Property>> superProperties = 
            new Hashtable<Property,Set<Property>>();
    
    private Hashtable<Category,Set<Category>> directSuperClasses = 
            new Hashtable<Category, Set<Category>>();
    private Hashtable<Property,Set<Property>> directSuperProperties =
            new Hashtable<Property,Set<Property>>();
    
    private Hashtable<Category,Set<Category>> subClasses = 
            new Hashtable<Category, Set<Category>>();
    private Hashtable<Property,Set<Property>> subProperties = 
            new Hashtable<Property,Set<Property>>();
    
    private Hashtable<Category,Set<Category>> directSubClasses = 
            new Hashtable<Category,Set<Category>>();
    private Hashtable<Property,Set<Property>> directSubProperties = 
            new Hashtable<Property,Set<Property>>();
    
    private Hashtable<Category,Set<Instance>> classInstances = 
            new Hashtable<Category, Set<Instance>>();
    private Hashtable<Category,Set<Instance>> directClassInstances = 
            new Hashtable<Category, Set<Instance>>();
    private Hashtable<Instance,Set<Category>> instancesOf = 
            new Hashtable<Instance,Set<Category>>();
    private Hashtable<Instance,Set<Category>> directInstancesOf = 
            new Hashtable<Instance,Set<Category>>();
    
    private Hashtable<Property,Set<Category>> propertyDomains = new Hashtable<Property, Set<Category>>();
    private Hashtable<Property,Set<Category>> propertyRanges = new Hashtable<Property, Set<Category>>();
    private Hashtable<Category,Set<Property>> domainOfProperties = new Hashtable<Category,Set<Property>>();
    private Hashtable<Category,Set<Property>> rangeOfProperties = new Hashtable<Category,Set<Property>>();
    
    private Category root = null;
    //private NamedGraph graph = new NamedGraph("Taxonomy");

    public Category getRoot() {
        return this.root;
    }

    public void setRoot(Category root) throws NoCategoryException,RootException {
        if (this.containCategory(root)) {
            if (this.directSuperClasses.get(root).isEmpty()) {
                this.root = root;
            }
            else {
                throw new RootException(root.getName() + " cannot have any super category.");
            }
        }
        else {
            throw new NoCategoryException(root.getName() + " is not in the taxonomy.");
        }
    }

    public boolean addCategory(Category c) {
        boolean result = false; 
        
        if (!this.containCategory(c)) {
            result =  this.categories.add(c);
            if (result) {
                this.classInstances.put(c, new HashSet<Instance>());
                this.directClassInstances.put(c, new HashSet<Instance>());
                this.subClasses.put(c, new HashSet<Category>());
                this.directSubClasses.put(c, new HashSet<Category>());
                this.superClasses.put(c, new HashSet<Category>());
                this.directSuperClasses.put(c, new HashSet<Category>());
                this.iClasses.put(c, new Hashtable<String,String>());
                this.domainOfProperties.put(c, new HashSet<Property>());
                this.rangeOfProperties.put(c, new HashSet<Property>());
            }
        }
        else {
            result = true;
        }
        
        return result;
    }

    public boolean addProperty(Property p) {
        boolean result = false;
        
        if (!this.containProperty(p)) {
            result = this.properties.add(p);
            if (result) {
                this.subProperties.put(p, new HashSet<Property>());
                this.directSubProperties.put(p, new HashSet<Property>());
                this.superProperties.put(p, new HashSet<Property>());
                this.directSuperProperties.put(p, new HashSet<Property>());
                this.iProperties.put(p, new Hashtable<String,String>());
                this.propertyDomains.put(p, new HashSet<Category>());
                this.propertyRanges.put(p, new HashSet<Category>());
            }
        }
        else {
            result = true;
        }
        
        return result;
    }

    public boolean addInstance(Instance i) {
        boolean result = false;
        
        if (!this.containInstance(i)) {
            result = this.instances.add(i);
            if (result) {
                this.instancesOf.put(i, new HashSet<Category>());
                this.directInstancesOf.put(i, new HashSet<Category>());
                this.iInstances.put(i, new Hashtable<String,String>());
            }
        }
        else {
            result = true;
        }
        
        return result;
    }

    public void subCategoryOf(Category c, Category parent) throws NoCategoryException, RootException {
        if (this.root != null && c == this.root) {
            throw new RootException(c.getName() + " cannot have any super category because it is root.");
        }
        else if (this.containCategory(c) && this.containCategory(parent)) {
            if (!this.directSuperClasses.get(c).contains(parent)) {
                /* I add 'c' category in the subclasses of 'parent' */
                this.directSubClasses.get(parent).add(c);
                this.subClasses.get(parent).add(c);
                
                /* I add 'parent' category in the superclasses of 'c' */
                this.directSuperClasses.get(c).add(parent);
                this.superClasses.get(c).add(parent);
                
                /* I add 'parent' as superclass of all the subclasses of 'c' */
                Iterator<Category> curSubClassesIte = this.subClasses.get(c).iterator();
                while (curSubClassesIte.hasNext()) {
                    this.superClasses.get(curSubClassesIte.next()).add(parent);
                }
                
                /* I add 'c' as subclass of all the superclasses of 'parent' */
                Iterator<Category> curSuperClassesIte = this.superClasses.get(parent).iterator();
                while (curSuperClassesIte.hasNext()) {
                    Category superCategory = curSuperClassesIte.next();
                    this.subClasses.get(superCategory).add(c);
                    this.superClasses.get(c).add(superCategory);
                }
                
                /* All the instances of 'c' become instances of 'parent' */
                Iterator<Instance> instancesIte = this.classInstances.get(c).iterator();
                while (instancesIte.hasNext()) {
                    Instance i = instancesIte.next();
                            
                    Iterator<Category> categoriesIte = this.superClasses.get(c).iterator();
                    while (categoriesIte.hasNext()) {
                        Category cur = categoriesIte.next();
                        this.classInstances.get(cur).add(i);
                        this.instancesOf.get(i).add(cur);
                    }
                }
            }            
        }
        else {
            throw new NoCategoryException("The classes " + c.getName() + " and/or " + parent.getName() + 
                    " are not in the taxonomy.");
        }
    }

    public void subPropertyOf(Property p, Property parent) throws NoPropertyException {
        if (this.containProperty(p) && this.containProperty(parent)) {
            if (!this.directSuperProperties.get(p).contains(parent)) {
                /* I add 'c' category in the subclasses of 'parent' */
                this.directSubProperties.get(parent).add(p);
                this.subProperties.get(parent).add(p);
                
                /* I add 'parent' category in the superclasses of 'c' */
                this.directSuperProperties.get(p).add(parent);
                this.superProperties.get(p).add(parent);
                
                /* I add 'parent' as superclass of all the subclasses of 'c' */
                Iterator<Property> curSubPropertiesIte = this.subProperties.get(p).iterator();
                while (curSubPropertiesIte.hasNext()) {
                    this.superProperties.get(curSubPropertiesIte.next()).add(parent);
                }
                
                /* I add 'c' as subclass of all the superclasses of 'parent' */
                Iterator<Property> curSuperPropertiesIte = this.superProperties.get(parent).iterator();
                while (curSuperPropertiesIte.hasNext()) {
                    this.subProperties.get(curSuperPropertiesIte.next()).add(p);
                }
            }
        }
        else {
            throw new NoPropertyException("The properties " + p.getName() + " and/or " + parent.getName() + 
                    " are not in the taxonomy.");
        }
    }

    public void instanceOf(Instance i, Category c) throws NoInstanceException, NoCategoryException {
        if (this.containCategory(c)) {
            if (this.containInstance(i)) {
                if (!this.directClassInstances.get(c).contains(i)) {
                    /* I add 'i' instance in the instances of 'c' */
                    this.directClassInstances.get(c).add(i);
                    this.classInstances.get(c).add(i);
                    
                    /* I add 'c' class in the classes of 'i' */
                    this.directInstancesOf.get(i).add(c);
                    this.instancesOf.get(i).add(c);
                    
                    /* I add 'i' as instance of all the superclasses of 'c' */
                    Iterator<Category> categoriesIte = this.superClasses.get(c).iterator();
                    while (categoriesIte.hasNext()) {
                        Category cur = categoriesIte.next();
                        this.classInstances.get(cur).add(i);
                        this.instancesOf.get(i).add(cur);
                    }
                }
            }
            else {
                throw new NoInstanceException("The instance " + i.getName() + " is not in the taxonomy.");
            }
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getAllSuperCategories(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.superClasses.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getAllSubCategories(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.subClasses.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getDirectSuperCategories(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.directSuperClasses.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getDirectSubCategories(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.directSubClasses.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getAllCategoriesByInstance(Instance i) throws NoInstanceException {
        if (this.containInstance(i)) {
            return this.instancesOf.get(i);
        }
        else {
            throw new NoInstanceException("The instance " + i.getName() + " is not in the taxonomy.");
        }
    }

    public Category getCategoryByName(String name) throws NoCategoryException {
        Iterator<Category> ite = this.categories.iterator();
        while (ite.hasNext()) {
            Category c = ite.next();
            if (c.getName().equals(name)) {
                return c;
            }
        }
        throw new NoCategoryException("The class " + name + " is not in the taxonomy.");
    }

    public void setDomain(Property p, Category domain) throws NoCategoryException, NoPropertyException {
        if (this.containProperty(p)) {
            if (this.containCategory(domain)) {
                this.propertyDomains.get(p).add(domain);
                this.domainOfProperties.get(domain).add(p);
            }
            else {
                throw new NoCategoryException("The class " + domain.getName() + " is not in the taxonomy.");
            }
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getDomain(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
        	return this.propertyDomains.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public void setRange(Property p, Category range) throws NoCategoryException, NoPropertyException {
        if (this.containProperty(p)) {
            if (this.containCategory(range)) {
            	this.propertyRanges.get(p).add(range);
            	this.rangeOfProperties.get(range).add(p);
            }
            else {
                throw new NoCategoryException("The class " + range.getName() + " is not in the taxonomy.");
            }
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Category> getRange(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
        	return this.propertyRanges.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Property> getAllSuperProperties(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
            return this.superProperties.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Property> getAllSubProperties(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
            return this.subProperties.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Property> getDirectSuperProperties(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
            return this.directSuperProperties.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Property> getDirectSubProperties(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
            return this.directSubProperties.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Property> getPropertiesByDomain(Category domain) throws NoCategoryException {
    	return this.domainOfProperties.get(domain);
    }

    public Set<Property> getPropertiesByRange(Category range) throws NoCategoryException {
    	return this.rangeOfProperties.get(range);
    }

    public Property getPropertyByName(String name) throws NoPropertyException {
        Iterator<Property> ite = this.properties.iterator();
        while (ite.hasNext()) {
            Property p = ite.next();
            if (p.getName().equals(name)) {
                return p;
            }
        }
        throw new NoPropertyException("The property " + name + " is not in the taxonomy.");
    }

    public Set<Instance> getAllInstances(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.classInstances.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Set<Instance> getDirectInstances(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.directClassInstances.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Instance getInstanceByName(String name) throws NoInstanceException {
        Iterator<Instance> ite = this.instances.iterator();
        while (ite.hasNext()) {
            Instance i = ite.next();
            if (i.getName().equals(name)) {
                return i;
            }
        }
        throw new NoInstanceException("The instance " + name + " is not in the taxonomy.");
    }

    public Set<Category> getAllCategories() {
        return this.categories;
    }

    public Set<Property> getAllProperties() {
        return this.properties;
    }

    public Set<Instance> getAllInstances() {
        return this.instances;
    }

    public boolean containCategory(Category c) {
        return this.categories.contains(c);
    }

    public boolean containProperty(Property p) {
        return this.properties.contains(p);
    }

    public boolean containInstance(Instance i) {
        return this.instances.contains(i);
    }

    public void addInfo(String key, String value) {
        this.iTaxonomy.put(key, value);
    }

    public void addInfo(Category c, String key, String value) throws NoCategoryException {
        if (this.containCategory(c)) {
        	Hashtable<String,String> table = this.iClasses.get(c);
        	if (table == null) {
        		table = new Hashtable<String,String>();
        		this.iClasses.put(c, table);
        	}
            table.put(key, value);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public void addInfo(Property p, String key, String value) throws NoPropertyException {
        if (this.containProperty(p)) {
            Hashtable<String,String> table = this.iProperties.get(p);
        	if (table == null) {
        		table = new Hashtable<String,String>();
        		this.iProperties.put(p, table);
        	}
            table.put(key, value);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public void addInfo(Instance i, String key, String value) throws NoInstanceException {
        if (this.containInstance(i)) {
            Hashtable<String,String> table = this.iInstances.get(i);
        	if (table == null) {
        		table = new Hashtable<String,String>();
        		this.iInstances.put(i, table);
        	}
            table.put(key, value);
        }
        else {
            throw new NoInstanceException("The instance " + i.getName() + " is not in the taxonomy.");
        }
    }

    public String getInfo(String key) {
        return this.iTaxonomy.get(key);
    }

    public String getInfo(Category c, String key) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.iClasses.get(c).get(key);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public String getInfo(Property p, String key) throws NoPropertyException {
        if (this.containProperty(p)) {
            return this.iProperties.get(p).get(key);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public String getInfo(Instance i, String key) throws NoInstanceException {
        if (this.containInstance(i)) {
            return this.iInstances.get(i).get(key);
        }
        else {
            throw new NoInstanceException("The instance " + i.getName() + " is not in the taxonomy.");
        }
    }

    public Map<String, String> getAllInfo() {
        return this.iTaxonomy;
    }

    public Map<String, String> getAllInfo(Category c) throws NoCategoryException {
        if (this.containCategory(c)) {
            return this.iClasses.get(c);
        }
        else {
            throw new NoCategoryException("The class " + c.getName() + " is not in the taxonomy.");
        }
    }

    public Map<String, String> getAllInfo(Property p) throws NoPropertyException {
        if (this.containProperty(p)) {
            return this.iProperties.get(p);
        }
        else {
            throw new NoPropertyException("The property " + p.getName() + " is not in the taxonomy.");
        }
    }

    public Map<String, String> getAllInfo(Instance i) throws NoInstanceException {
        if (this.containInstance(i)) {
            return this.iInstances.get(i);
        }
        else {
            throw new NoInstanceException("The instance " + i.getName() + " is not in the taxonomy.");
        }
    }

    public void setDomain(String property, Category domain) throws NoCategoryException, NoPropertyException {
        this.setDomain(this.getPropertyByName(property), domain);
    }

    public Set<Category> getDomain(String property) throws NoPropertyException {
        return this.getDomain(this.getPropertyByName(property));
    }

    public void setRange(String property, Category domain) throws NoCategoryException, NoPropertyException {
        this.setRange(this.getPropertyByName(property), domain);
    }

    public Set<Category> getRange(String property) throws NoPropertyException {
        return this.getRange(this.getPropertyByName(property));
    }
    
    public Object accept(Visitor v) {
        return v.visit(this);
    }

    public Set<Category> getDirectCategoriesByInstance(Instance i) throws NoInstanceException {
        if (this.containInstance(i)) {
            return this.directInstancesOf.get(i);
        }
        else {
            throw new NoInstanceException("The instance " + i.getName() + " is not in the taxonomy.");
        }
    }

	@Override
	public void flushInfo() {
		iTaxonomy = new Hashtable<String, String>();
	}

	@Override
	public void flushCategoryInfo() {
	    iClasses = new Hashtable<Category, Hashtable<String, String>>();
	}

	@Override
	public void flushPropertyInfo() {
	    iProperties = new Hashtable<Property, Hashtable<String, String>>();
	}

	@Override
	public void flushInstanceInfo() {
	    iInstances = new Hashtable<Instance, Hashtable<String, String>>();
	}
        
}
