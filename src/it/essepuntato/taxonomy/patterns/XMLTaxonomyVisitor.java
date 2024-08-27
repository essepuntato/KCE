package it.essepuntato.taxonomy.patterns;

import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.Instance;
import it.essepuntato.taxonomy.Property;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.taxonomy.exceptions.NoInstanceException;
import it.essepuntato.taxonomy.exceptions.NoPropertyException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Silvio Peroni
 */
public class XMLTaxonomyVisitor implements Visitor {
    
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder = null;
    private Document doc = null;
    private HTaxonomy ht = null;
    private static String ns = "http://www.essepuntato.it/taxonomy";
    
    public XMLTaxonomyVisitor() throws ParserConfigurationException {
        this.factory.setNamespaceAware(true); // never forget this!
        this.builder = factory.newDocumentBuilder();
        this.doc = this.builder.newDocument();
    }

    public Object visit(Category c) {
        Element category = this.doc.createElementNS(ns, "class");
        category.setAttribute("name", c.getName());
        
        if (this.ht != null) {
            try {
                Iterator<Category> ite = this.ht.getDirectSuperCategories(c).iterator();
                while (ite.hasNext()) {
                    Element subCategoryOf = this.doc.createElementNS(ns, "subClassOf");
                    subCategoryOf.setAttribute("name", ite.next().getName());
                    category.appendChild(subCategoryOf);
                }
                
                Iterator<String> iteInfo = this.ht.getAllInfo(c).keySet().iterator();
                while (iteInfo.hasNext()) {
                    String s = iteInfo.next();
                    Element info = this.doc.createElementNS(ns, "data");
                    info.setAttribute("key", s);
                    info.setAttribute("value", this.ht.getInfo(c,s));
                    category.appendChild(info);
                }
            } catch (NoCategoryException ex) {
                System.err.println("The class '" + c.getName() + "' is not in the taxonomy");
            }
        }
        
        return category;
    }

    public Object visit(Property p) {
        Element property = this.doc.createElementNS(ns, "property");
        property.setAttribute("name", p.getName());
        
        if (this.ht != null) {
            try {
            	property.setAttribute("domain", ht.getDomain(p).toString());
            	property.setAttribute("range", ht.getRange(p).toString());
            	
                Iterator<Property> ite = this.ht.getDirectSuperProperties(p).iterator();
                while (ite.hasNext()) {
                    Element subPropertyOf = this.doc.createElementNS(ns, "subPropertyOf");
                    subPropertyOf.setAttribute("name", ite.next().getName());
                    property.appendChild(subPropertyOf);
                }
                
                Iterator<String> iteInfo = this.ht.getAllInfo(p).keySet().iterator();
                while (iteInfo.hasNext()) {
                    String s = iteInfo.next();
                    Element info = this.doc.createElementNS(ns, "data");
                    info.setAttribute("key", s);
                    info.setAttribute("value", this.ht.getInfo(p,s));
                    property.appendChild(info);
                }
            } catch (NoPropertyException ex) {
                System.err.println("The property '" + p.getName() + "' is not in the taxonomy");
            }
        }
        
        return property;
    }

    public Object visit(Instance i) {
        Element instance = this.doc.createElementNS(ns, "instance");
        instance.setAttribute("name", i.getName());
        
        if (this.ht != null) {
            try {
                Iterator<Category> ite = this.ht.getDirectCategoriesByInstance(i).iterator();
                while (ite.hasNext()) {
                    Element instanceOf = this.doc.createElementNS(ns, "instanceOf");
                    instanceOf.setAttribute("name", ite.next().getName());
                    instance.appendChild(instanceOf);
                }
                
                Iterator<String> iteInfo = this.ht.getAllInfo(i).keySet().iterator();
                while (iteInfo.hasNext()) {
                    String s = iteInfo.next();
                    Element info = this.doc.createElementNS(ns, "data");
                    info.setAttribute("key", s);
                    info.setAttribute("value", this.ht.getInfo(i,s));
                    instance.appendChild(info);
                }
            } catch (NoInstanceException ex) {
                System.err.println("The instance '" + i.getName() + "' is not in the taxonomy");
            }
        }
        
        return instance;
    }

    public Object visit(HTaxonomy t) {
        this.ht = t;
        
        Element root = this.doc.createElementNS(ns, "taxonomy");
        this.doc.appendChild(root);
        
        Element classes = this.doc.createElementNS(ns, "classes");
        root.appendChild(classes);
        Iterator<Category> cIte = this.ht.getAllCategories().iterator();
        while (cIte.hasNext()) {
            classes.appendChild((Node) cIte.next().accept(this));
        }
        
        Element properties = this.doc.createElementNS(ns, "properties");
        root.appendChild(properties);
        Iterator<Property> pIte = this.ht.getAllProperties().iterator();
        while (pIte.hasNext()) {
            properties.appendChild((Node) pIte.next().accept(this));
        }
        
        Element instances = this.doc.createElementNS(ns, "instances");
        root.appendChild(instances);
        Iterator<Instance> iIte = this.ht.getAllInstances().iterator();
        while (iIte.hasNext()) {
            instances.appendChild((Node) iIte.next().accept(this));
        }
                
        Iterator<String> iteInfo = this.ht.getAllInfo().keySet().iterator();
        while (iteInfo.hasNext()) {
            String s = iteInfo.next();
            Element info = this.doc.createElementNS(ns, "data");
            info.setAttribute("key", s);
            info.setAttribute("value", this.ht.getInfo(s));
            root.appendChild(info);
        }
        
        return this.doc;
    }

}
