package it.essepuntato.taxonomy.importer;

import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.Instance;
import it.essepuntato.taxonomy.Property;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.taxonomy.exceptions.NoInstanceException;
import it.essepuntato.taxonomy.exceptions.NoPropertyException;
import it.essepuntato.taxonomy.exceptions.RootException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Silvio Peroni
 */
public class XMLTaxonomyImporter {
    
    private ArrayList<Category> categories = new ArrayList<Category>();
    private ArrayList<Property> properties = new ArrayList<Property>();
    private ArrayList<Instance> instances = new ArrayList<Instance>();
    
    private Hashtable<String,String> domains = new Hashtable<String, String>();
    private Hashtable<String,String> ranges = new Hashtable<String, String>();
    
    private Hashtable<String,String> iTaxonomy = new Hashtable<String, String>();
    private Hashtable<String,Hashtable<String,String>> iClasses = 
            new Hashtable<String, Hashtable<String, String>>();
    private Hashtable<String,Hashtable<String,String>> iProperties = 
            new Hashtable<String, Hashtable<String, String>>();
    private Hashtable<String,Hashtable<String,String>> iInstances = 
            new Hashtable<String, Hashtable<String, String>>();
    
    private Hashtable<String,List<String>> directSubClasses = 
            new Hashtable<String,List<String>>();
    private Hashtable<String,List<String>> directSubProperties = 
            new Hashtable<String,List<String>>();
    private Hashtable<String,List<String>> directInstancesOf = 
            new Hashtable<String,List<String>>();
    
    public HTaxonomy importFromXML(Document doc) {
        Node node = doc.getDocumentElement();
        this.visitNode(node,node.getNodeName(),null);
        
        HTaxonomy result = this.fillHTaxonomy();
        result = this.setRoot(result);
        
        return result;
    }

    private HTaxonomy fillHTaxonomy() {
        HTaxonomy t = new HTaxonomy();
        
        Iterator<String> tInfo = this.iTaxonomy.keySet().iterator();
        while (tInfo.hasNext()) {
            String key = tInfo.next();
            t.addInfo(key, this.iTaxonomy.get(key));
        }
        
        Iterator<Category> cIte = this.categories.iterator();
        while (cIte.hasNext()) {
            Category c = cIte.next();
            t.addCategory(c);
            
            Iterator<String> info = this.iClasses.get(c.getName()).keySet().iterator();
            while (info.hasNext()) {
                String key = info.next();
                try {
                    t.addInfo(c, key, this.iClasses.get(c.getName()).get(key));
                } catch (NoCategoryException ex) {
                    System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The class '" + c.getName() 
                            + "' is not in the taxonomy.");
                }
            }
        }
        
        Iterator<Property> pIte = this.properties.iterator();
        while (pIte.hasNext()) {
            Property p = pIte.next();
            t.addProperty(p);
            
            Iterator<String> info = this.iProperties.get(p.getName()).keySet().iterator();
            while (info.hasNext()) {
                String key = info.next();
                try {
                    t.addInfo(p, key, this.iProperties.get(p.getName()).get(key));
                } catch (NoPropertyException ex) {
                    System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The property '" 
                            + p.getName() + "' is not in the taxonomy.");
                }
            }
            
            String domain = this.domains.get(p.getName());
            String range = this.ranges.get(p.getName());
            try {
                if (!domain.equals("")) {
                    t.setDomain(p, t.getCategoryByName(domain));
                }
                if (!range.equals("")) {
                    t.setRange(p, t.getCategoryByName(range));
                }
            } catch (NoPropertyException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The property '" 
                        + p.getName() + "' is not in the taxonomy.");
            } catch (NoCategoryException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The domain/range category" 
                        + " of the property '" + p.getName() + "' is not in the taxonomy.");
            }
        }
        
        Iterator<Instance> iIte = this.instances.iterator();
        while (iIte.hasNext()) {
            Instance i = iIte.next();
            t.addInstance(i);
            
            Iterator<String> info = this.iInstances.get(i.getName()).keySet().iterator();
            while (info.hasNext()) {
                String key = info.next();
                try {
                    t.addInfo(i, key, this.iInstances.get(i.getName()).get(key));
                } catch (NoInstanceException ex) {
                    System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The instance '" 
                            + i.getName() + "' is not in the taxonomy.");
                }
            }
        }
        
        Iterator<String> iClass = this.directSubClasses.keySet().iterator();
        while (iClass.hasNext()) {
            String name = iClass.next();
            try {
                Category c = t.getCategoryByName(name);
                Iterator<String> iSubClass = this.directSubClasses.get(name).iterator();
                while (iSubClass.hasNext()) {
                    Category subC = t.getCategoryByName(iSubClass.next());
                    t.subCategoryOf(subC, c);
                }
                
            } catch (RootException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - A subclass of '" + name 
                        + "' is the root category.");
            } catch (NoCategoryException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The class '" + name 
                        + "' or one of its subclasses is not in the taxonomy.");
            }
        }
        
        Iterator<String> iProperty = this.directSubProperties.keySet().iterator();
        while (iProperty.hasNext()) {
            String name = iProperty.next();
            try {
                Property p = t.getPropertyByName(name);
                Iterator<String> iSubProperty = this.directSubProperties.get(name).iterator();
                while (iSubProperty.hasNext()) {
                    Property subP = t.getPropertyByName(iSubProperty.next());
                    t.subPropertyOf(subP, p);
                }    
            } catch (NoPropertyException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The property '" + name 
                        + "' or one of its subproperties is not in the taxonomy.");
            }
        }
        
        Iterator<String> iInstance = this.directInstancesOf.keySet().iterator();
        while (iInstance.hasNext()) {
            String name = iInstance.next();
            try {
                Instance i = t.getInstanceByName(name);
                Iterator<String> iInstanceOf = this.directInstancesOf.get(name).iterator();
                while (iInstanceOf.hasNext()) {
                    String cName = iInstanceOf.next();
                    try {
                        Category c = t.getCategoryByName(cName);
                        t.instanceOf(i, c);
                    } catch (NoCategoryException ex) {
                        System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The class '" + 
                                cName + "'  is not in the taxonomy.");
                    }
                }    
            } catch (NoInstanceException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The instance '" + name 
                        + "' is not in the taxonomy.");
            }
        }
        
        return t;
    }
    
    private String getId(Node node) {
        return this.getAttr(node, "name");
    }
    
    private String getAttr(Node node, String name) {
        String result = null;
        NamedNodeMap attrs = node.getAttributes();
        if (attrs.getLength() > 0) {
            Node id = attrs.getNamedItem(name);
            result = (id == null ? null : id.getNodeValue());
        }
        
        return result;
    }
    
    private void visitNodes(NodeList l, String parent, String parentId) {
        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                this.visitNode(n, parent, parentId);
            }
        }
    }
    
    private void visitNode(Node n, String parent, String parentId) {
        String name = n.getNodeName();
        String curId = this.getId(n);
        
        if (name.equals("classes")) {
            this.visitNodes(n.getChildNodes(), name, curId);
        }
        else if (name.equals("properties")) {
            this.visitNodes(n.getChildNodes(), name, curId);
        }
        else if (name.equals("instances")) {
            this.visitNodes(n.getChildNodes(), name, curId);
        }
        else if (name.equals("class")) {
            this.categories.add(new Category(curId));
            this.iClasses.put(curId, new Hashtable<String,String>());
            this.visitNodes(n.getChildNodes(), name, curId);
        }
        else if (name.equals("property")) {
            this.properties.add(new Property(curId));
            this.iProperties.put(curId, new Hashtable<String,String>());
            
            String domain = this.getAttr(n, "domain");
            this.domains.put(curId, (domain == null ? "" : domain));
            String range = this.getAttr(n, "range");
            this.ranges.put(curId, (range == null ? "" : range));
            this.visitNodes(n.getChildNodes(), name, curId);
        }
        else if (name.equals("instance")) {
            this.instances.add(new Instance(curId));
            this.iInstances.put(curId, new Hashtable<String,String>());
            this.visitNodes(n.getChildNodes(), name, curId);
        }
        else if (name.equals("data")) {
            String key = n.getAttributes().getNamedItem("key").getNodeValue();
            String value = n.getAttributes().getNamedItem("value").getNodeValue();
            if (parent.equals("class")) {
                Map<String,String> map = this.iClasses.get(parentId);
                map.put(key, value);
            }
            else if (parent.equals("property")) {
                Map<String,String> map = this.iProperties.get(parentId);
                map.put(key, value);
            }
            else if (parent.equals("instance")) {
                Map<String,String> map = this.iInstances.get(parentId);
                map.put(key, value);
            }
            else {
                this.iTaxonomy.put(key, value);
            }
        }
        else if (name.equals("taxonomy")) {
            this.visitNodes(n.getChildNodes(), n.getNodeName(), curId);
        }
        else if (name.equals("subClassOf")) {
            List<String> cur = this.directSubClasses.get(curId);
            if (cur == null) {
                cur = new ArrayList<String>();
                this.directSubClasses.put(curId, cur);
            }
            cur.add(parentId);
        }
        else if (name.equals("subPropertyOf")) {
            List<String> cur = this.directSubProperties.get(curId);
            if (cur == null) {
                cur = new ArrayList<String>();
                this.directSubProperties.put(curId, cur);
            }
            cur.add(parentId);
        }
        else if (name.equals("instanceOf")) {
            List<String> cur = this.directInstancesOf.get(curId);
            if (cur == null) {
                cur = new ArrayList<String>();
                this.directInstancesOf.put(parentId, cur);
            }
            cur.add(curId);
        }
    }

    private HTaxonomy setRoot(HTaxonomy t) {
        Iterator<Category> ite = t.getAllCategories().iterator();
        while (ite.hasNext() && t.getRoot() == null) {
            Category c = ite.next();
            try {
                if (t.getDirectSuperCategories(c).isEmpty()) {
                    t.setRoot(c);
                    System.out.println("The class '" + c.getName() + "' is the root.");
                }
            } catch (RootException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - A class '" + c.getName() 
                        + "' has some superclasses.");
            } catch (NoCategoryException ex) {
                System.err.println("[XMLTaxonomyImporter - fillTaxonomy] WARNING - The class '" + c.getName() 
                        + "' is not in the taxonomy.");
            }
        }
        return t;
    }
}
