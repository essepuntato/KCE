package it.essepuntato.taxonomy;

import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.taxonomy.exceptions.NoInstanceException;
import it.essepuntato.taxonomy.exceptions.NoPropertyException;
import it.essepuntato.taxonomy.exceptions.RootException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silvio Peroni
 */
public interface Taxonomy {
    public Category getRoot();
    public void setRoot(Category root) throws NoCategoryException,RootException;
    
    public boolean addCategory(Category c);
    public boolean addProperty(Property p);
    public boolean addInstance(Instance i);
    
    public void subCategoryOf(Category c, Category parent) throws NoCategoryException,RootException;
    public void subPropertyOf(Property p, Property parent) throws NoPropertyException;
    public void instanceOf(Instance i, Category c) throws NoCategoryException,NoInstanceException;
    
    public Set<Category> getAllSuperCategories(Category c) throws NoCategoryException;
    public Set<Category> getAllSubCategories(Category c) throws NoCategoryException;
    public Set<Category> getDirectSuperCategories(Category c) throws NoCategoryException;
    public Set<Category> getDirectSubCategories(Category c) throws NoCategoryException;
    public Set<Category> getAllCategoriesByInstance(Instance i) throws NoInstanceException;
    public Set<Category> getDirectCategoriesByInstance(Instance i) throws NoInstanceException;
    public Category getCategoryByName(String name) throws NoCategoryException;
    
    public void setDomain(Property p, Category domain) throws NoCategoryException,NoPropertyException;
    public Set<Category> getDomain(Property p) throws NoPropertyException;
    public void setRange(Property p, Category domain) throws NoCategoryException,NoPropertyException;
    public Set<Category> getRange(Property p) throws NoPropertyException;
    
    public void setDomain(String property, Category domain) throws NoCategoryException,NoPropertyException;
    public Set<Category> getDomain(String property) throws NoPropertyException;
    public void setRange(String property, Category domain) throws NoCategoryException,NoPropertyException;
    public Set<Category> getRange(String property) throws NoPropertyException;
    
    public Set<Property> getAllSuperProperties(Property p) throws NoPropertyException;
    public Set<Property> getAllSubProperties(Property p) throws NoPropertyException;
    public Set<Property> getDirectSuperProperties(Property p) throws NoPropertyException;
    public Set<Property> getDirectSubProperties(Property p) throws NoPropertyException;
    public Set<Property> getPropertiesByDomain(Category domain) throws NoCategoryException;
    public Set<Property> getPropertiesByRange(Category range) throws NoCategoryException;
    public Property getPropertyByName(String name) throws NoPropertyException;
    
    public Set<Instance> getAllInstances(Category c) throws NoCategoryException;
    public Set<Instance> getDirectInstances(Category c) throws NoCategoryException;
    public Instance getInstanceByName(String name) throws NoInstanceException;
    
    public Set<Category> getAllCategories();
    public Set<Property> getAllProperties();
    public Set<Instance> getAllInstances();
    
    public boolean containCategory(Category c);
    public boolean containProperty(Property p);
    public boolean containInstance(Instance i);
    
    public void addInfo(String key, String value);
    public void addInfo(Category c, String key, String value) throws NoCategoryException;
    public void addInfo(Property p, String key, String value) throws NoPropertyException;
    public void addInfo(Instance i, String key, String value) throws NoInstanceException;
    
    public String getInfo(String key);
    public String getInfo(Category c, String key) throws NoCategoryException;
    public String getInfo(Property p, String key) throws NoPropertyException;
    public String getInfo(Instance i, String key) throws NoInstanceException;
    
    public Map<String,String> getAllInfo();
    public Map<String,String> getAllInfo(Category c) throws NoCategoryException;
    public Map<String,String> getAllInfo(Property p) throws NoPropertyException;
    public Map<String,String> getAllInfo(Instance i) throws NoInstanceException;
    
    public void flushInfo();
    public void flushCategoryInfo();
    public void flushPropertyInfo();
    public void flushInstanceInfo();
}
