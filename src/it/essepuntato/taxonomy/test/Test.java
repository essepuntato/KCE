package it.essepuntato.taxonomy.test;

import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.Instance;
import it.essepuntato.taxonomy.Property;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.taxonomy.exceptions.NoInstanceException;
import it.essepuntato.taxonomy.exceptions.NoPropertyException;
import it.essepuntato.taxonomy.exceptions.RootException;
import it.essepuntato.taxonomy.importer.XMLTaxonomyImporter;
import it.essepuntato.taxonomy.patterns.Visitor;
import it.essepuntato.taxonomy.patterns.XMLTaxonomyVisitor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Silvio Peroni
 */
public class Test {
    public static void main(String[] args) {
        try {
            HTaxonomy t = new HTaxonomy();

            Category thing = new Category("Thing");
            Category person = new Category("Person");
            Category man = new Category("Man");
            Category woman = new Category("Woman");
            Category job = new Category("Job");

            t.addCategory(thing);
            t.addCategory(person);
            t.addCategory(man);
            t.addCategory(woman);
            t.addCategory(job);
            
            t.setRoot(thing);
            t.subCategoryOf(person, thing);
            t.subCategoryOf(job, thing);
            t.subCategoryOf(man, person);
            t.subCategoryOf(woman, person);
            
            Property hasJob = new Property("hasJob");
            Property fatherOf = new Property("fatherOf");
            Property parentOf = new Property("parentOf");
            
            t.addProperty(hasJob);
            t.addProperty(fatherOf);
            t.addProperty(parentOf);
            
            t.subPropertyOf(fatherOf, parentOf);
            t.setRange(hasJob, job);
            t.setDomain(parentOf, person);
            t.setRange(parentOf, person);
            t.setDomain(fatherOf, man);
            
            Instance bob = new Instance("bob");
            Instance chares = new Instance("chares");
            Instance alice = new Instance("alice");
            Instance pippo = new Instance("pippo");
            Instance scientist = new Instance("scientist");
            
            t.addInstance(bob);
            t.addInstance(chares);
            t.addInstance(alice);
            t.addInstance(pippo);
            t.addInstance(scientist);
            
            t.instanceOf(bob, man);
            t.instanceOf(chares, man);
            t.instanceOf(alice, woman);
            t.instanceOf(pippo, person);
            t.instanceOf(scientist, job);
            
            t.addInfo("pippo", "pluto");
            t.addInfo(person,"paperino", "topolino");
            t.addInfo(person,"paperino", "minni");
            t.addInfo(man,"qui", "quo");
            
            t.addInfo(hasJob,"bugs", "bunny");
            
            t.addInfo(alice,"forse", "piove");
            
            System.out.println(t.getAllCategoriesByInstance(bob));
            
            Visitor v = new XMLTaxonomyVisitor();
            Document doc = (Document) t.accept(v);
            /*
            XMLDocumentHandler h = XMLDocumentHandler.getInstance();
            h.save(doc, new File("prova.xml"));
            
            XMLTaxonomyImporter i = new XMLTaxonomyImporter();
            HTaxonomy newT = i.importFromXML(h.load(new File("prova.xml")));
            v = new XMLTaxonomyVisitor();
            Document newDoc = (Document) newT.accept(v);
            h.save(newDoc, new File("newProva.xml"));
            */
        } catch (NoInstanceException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoCategoryException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }/* catch (SAXException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SerializeXMLDocumentException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }*/ catch (ParserConfigurationException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }/* catch (MakeXMLDocumentHandlerException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }*/ catch (NoPropertyException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RootException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
