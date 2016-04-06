**How to cite:** Peroni, S., Motta, E., d’Aquin, M. (2008). Identifying key concepts in an ontology, through the integration of cognitive principles with statistical and topological measures. In Domingue, J., Anutariya, C. (Eds.), The Semantic Web - Proceedings of the 3rd Asian Semantic Web Conference (ASWC 2008), Lecture Notes in Computer Science 5367: 242-256. Berlin, Germany: Springer. http://dx.doi.org/10.1007/978-3-540-89704-0_17

# How to use it within a Java program

<pre>
String ontologyURL = "...";
boolean considerImportedOntologies = true;
HTaxonomy ht = new OWLAPITaxonomyMaker(ontologyURL, considerImportedOntologies).makeTaxonomy();
Engine e = new Engine(ht);
e.setSequence(true);
e.run();
Set<String> result = e.getKeyConcepts();
</pre>

# How to use it online

The service is available at http://www.essepuntato.it/kce