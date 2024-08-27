package it.essepuntato.semanticweb.owlapi;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;

public class OWLAPIManager {
	private Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private OWLClass rootClass;

	public OWLAPIManager(OWLOntology ontology, boolean useImports) {
		ontologies.add(ontology);
		
		if(useImports) {
			ontologies.addAll(ontology.getImports());
		}
		
		rootClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
	}
	
	public Set<OWLClass> getAllClasses() {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		for (OWLOntology ontology : ontologies) {
			result.addAll(ontology.classesInSignature(Imports.INCLUDED).toList());
		}
		
		return result;
	}
	
	public Set<OWLClass> getDirectSubClasses(OWLClass aClass) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		for (OWLClassExpression aSubClass : EntitySearcher.getSubClasses(aClass, ontologies.stream()).toList()) {
			if (aSubClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
				result.add((OWLClass) aSubClass);
			}
		}
		
		return result;
	}
	
	public Set<OWLClass> getAllSubClasses(OWLClass aClass) {
		return getAllSubClasses(aClass, new HashSet<OWLClass>());
	}
	
	private Set<OWLClass> getAllSubClasses(OWLClass aClass, Set<OWLClass> visited) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		if (!visited.contains(aClass)) {
			visited.add(aClass);
			
			for (OWLClass aSubClass : getDirectSubClasses(aClass)) {
				result.add(aSubClass);
				result.addAll(getAllSubClasses(aSubClass, visited));
			}
		}
		
		return result;
	}
	
	public Set<OWLClass> getDirectSuperClasses(OWLClass aClass) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		for (OWLClassExpression aSuperClass : EntitySearcher.getSuperClasses(aClass, ontologies.stream()).toList()) {
			if (aSuperClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
				result.add((OWLClass) aSuperClass);
			}
		}
		
		return result;
	}
	
	public Set<OWLClass> getAllSuperClasses(OWLClass aClass) {
		return getAllSuperClasses(aClass, new HashSet<OWLClass>());
	}
	
	private Set<OWLClass> getAllSuperClasses(OWLClass aClass, Set<OWLClass> visited) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		if (!visited.contains(aClass)) {
			visited.add(aClass);
			
			for (OWLClass aSuperClass : getDirectSuperClasses(aClass)) {
				result.add(aSuperClass);
				result.addAll(getAllSuperClasses(aSuperClass, visited));
			}
		}
		
		return result;
	}
	
	public OWLClass getRootClass() {
		 return rootClass;
	}
	
	public Set<OWLObjectProperty> getAllObjectProperties() {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		for (OWLOntology ontology : ontologies) {
			result.addAll(ontology.objectPropertiesInSignature(Imports.INCLUDED).toList());
		}
		
		return result;
	}
	
	public Set<OWLObjectProperty> getDirectSubObjectProperties(OWLObjectProperty aProperty) {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		for (OWLObjectPropertyExpression aSubProperty : EntitySearcher.getSubProperties(aProperty, ontologies.stream()).toList()) {
			result.add(aSubProperty.asOWLObjectProperty());
		}
		
		return result;
	}
	
	public Set<OWLObjectProperty> getAllSubObjectProperties(OWLObjectProperty aProperty) {
		return getAllSubObjectProperties(aProperty, new HashSet<OWLObjectProperty>());
	}
	
	private Set<OWLObjectProperty> getAllSubObjectProperties(
			OWLObjectProperty aProperty, Set<OWLObjectProperty> visited) {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		if (!visited.contains(aProperty)) {
			visited.add(aProperty);
			
			for (OWLObjectProperty aSubObjectProperty : getDirectSubObjectProperties(aProperty)) {
				result.add(aSubObjectProperty);
				result.addAll(getAllSubObjectProperties(aSubObjectProperty, visited));
			}
		}
		
		return result;
	}
	
	public Set<OWLObjectProperty> getDirectSuperObjectProperties(OWLObjectProperty aProperty) {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		for (OWLObjectPropertyExpression aSuperProperty : EntitySearcher.getSuperProperties(aProperty, ontologies.stream()).toList()) { 
			result.add(aSuperProperty.asOWLObjectProperty());
		}
		
		return result;
	}
	
	public Set<OWLObjectProperty> getAllSuperObjectProperties(OWLObjectProperty aProperty) {
		return getAllSuperObjectProperties(aProperty, new HashSet<OWLObjectProperty>());
	}
	
	private Set<OWLObjectProperty> getAllSuperObjectProperties(
			OWLObjectProperty aProperty, Set<OWLObjectProperty> visited) {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		if (!visited.contains(aProperty)) {
			visited.add(aProperty);
			
			for (OWLObjectProperty aSuperObjectProperty : getDirectSuperObjectProperties(aProperty)) {
				result.add(aSuperObjectProperty);
				result.addAll(getAllSuperObjectProperties(aSuperObjectProperty, visited));
			}
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getAllDataProperties() {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		for (OWLOntology ontology : ontologies) {
			result.addAll(ontology.dataPropertiesInSignature(Imports.INCLUDED).toList());
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getDirectSubDataProperties(OWLDataProperty aProperty) {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		for (OWLDataPropertyExpression aSubProperty : EntitySearcher.getSubProperties(aProperty, ontologies.stream()).toList()) {
			result.add(aSubProperty.asOWLDataProperty());
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getAllSubDataProperties(OWLDataProperty aProperty) {
		return getAllSubDataProperties(aProperty, new HashSet<OWLDataProperty>());
	}
	
	private Set<OWLDataProperty> getAllSubDataProperties(
			OWLDataProperty aProperty, Set<OWLDataProperty> visited) {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		if (!visited.contains(aProperty)) {
			visited.add(aProperty);
			
			for (OWLDataProperty aSubDataProperty : getDirectSubDataProperties(aProperty)) {
				result.add(aSubDataProperty);
				result.addAll(getAllSubDataProperties(aSubDataProperty, visited));
			}
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getDirectSuperDataProperties(OWLDataProperty aProperty) {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		for (OWLDataPropertyExpression aSuperProperty : EntitySearcher.getSuperProperties(aProperty, ontologies.stream()).toList()) { 
			result.add(aSuperProperty.asOWLDataProperty());
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getAllSuperDataProperties(OWLDataProperty aProperty) {
		return getAllSuperDataProperties(aProperty, new HashSet<OWLDataProperty>());
	}
	
	private Set<OWLDataProperty> getAllSuperDataProperties(
			OWLDataProperty aProperty, Set<OWLDataProperty> visited) {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		if (!visited.contains(aProperty)) {
			visited.add(aProperty);
			
			for (OWLDataProperty aSuperDataProperty : getDirectSuperDataProperties(aProperty)) {
				result.add(aSuperDataProperty);
				result.addAll(getAllSuperDataProperties(aSuperDataProperty, visited));
			}
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getAllDataPropertyHavingTheClassAsDomain(OWLClass aClass) {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		for (OWLDataProperty aProperty : getAllDataProperties()) {
			Set<OWLClassExpression> domains = EntitySearcher.getDomains(aProperty,ontologies.stream()).collect(Collectors.toSet()); 
			if (domains.contains(aClass)) {
				result.add(aProperty);
			}
		}
		
		return result;
	}
	
	public Set<OWLDataProperty> getAllDataPropertyHavingTheClassAsRange(OWLClass aClass) {
		Set<OWLDataProperty> result = new HashSet<OWLDataProperty>();
		
		for (OWLDataProperty aProperty : getAllDataProperties()) {
			Set<OWLDataRange> ranges = EntitySearcher.getRanges(aProperty, ontologies.stream()).collect(Collectors.toSet()); 
			if (ranges.contains(aClass)) {
				result.add(aProperty);
			}
		}
		
		return result;
	}
	
	public Set<OWLObjectProperty> getAllObjectPropertyHavingTheClassAsDomain(OWLClass aClass) {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		for (OWLObjectProperty aProperty : getAllObjectProperties()) {
			Set<OWLClassExpression> domains = EntitySearcher.getDomains(aProperty, ontologies.stream()).collect(Collectors.toSet()); 
			if (domains.contains(aClass)) {
				result.add(aProperty);
			}
		}
		return result;
	}
	
	public Set<OWLObjectProperty> getAllObjectPropertyHavingTheClassAsRange(OWLClass aClass) {
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		
		for (OWLObjectProperty aProperty : getAllObjectProperties()) {
			Set<OWLClassExpression> ranges = EntitySearcher.getRanges(aProperty, ontologies.stream()).collect(Collectors.toSet()); 
			if (ranges.contains(aClass)) {
				result.add(aProperty);
			}
		}
		
		return result;
	}
	
	public Set<OWLClassExpression> getAllDomainClasses(OWLObjectProperty aProperty) {
		Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();
		result = EntitySearcher.getDomains(aProperty, ontologies.stream()).collect(Collectors.toSet());
		return result; 
	}
	
	public Set<OWLClassExpression> getAllRangeClasses(OWLObjectProperty aProperty) {
		Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();		
		result = EntitySearcher.getRanges(aProperty, ontologies.stream()).collect(Collectors.toSet()); 		
		return result;
	}
	
	public Set<OWLIndividual> getDirectIndividuals(OWLClass aClass) {
		Set<OWLIndividual> results = new HashSet<>();
		results = EntitySearcher.getIndividuals(aClass, ontologies.stream()).collect(Collectors.toSet()); 
		return results;
	}
	
	public Set<OWLIndividual> getAllIndividuals(OWLClass aClass) {
		Set<OWLIndividual> result = new HashSet<OWLIndividual>();
		
		result.addAll(getDirectIndividuals(aClass));
		
		for (OWLClass aSubClass : getAllSubClasses(aClass)) {
			result.addAll(getDirectIndividuals(aSubClass));
		}
		
		return result;
	}
	
	public Set<OWLClass> getDirectTypes(OWLIndividual anIndividual) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		for (OWLClassExpression aClass : EntitySearcher.getTypes(anIndividual, ontologies.stream()).toList()) {
			if (aClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
				result.add(aClass.asOWLClass());
			}
		}
		
		return result;
	}
	
	public Set<OWLClass> getAllTypes(OWLIndividual anIndividual) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		
		Set<OWLClass> directTypes = getDirectTypes(anIndividual);
		for (OWLClass aClass : directTypes) {
			result.addAll(getAllSuperClasses(aClass));
		}
		result.addAll(directTypes);
		
		return result;
	}
}
