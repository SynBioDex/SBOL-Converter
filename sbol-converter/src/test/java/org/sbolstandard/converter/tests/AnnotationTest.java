package org.sbolstandard.converter.tests;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

//import javax.xml.namespace.QName;



import org.junit.jupiter.api.Test;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

/**
 * Unit test for simple App.
 */
public class AnnotationTest {


	
	@Test
	public void TestSBOL2Annotation() throws Exception {
		String prURI="http://partsregistry.org/";	
		String annoNS="http://keele.ac.uk/";	
		
		String prPrefix="pr";	
		
		SBOLDocument document = new SBOLDocument();				
		document.setTypesInURIs(true);
		document.addNamespace(URI.create(prURI), prPrefix);
		document.setDefaultURIprefix(prURI);	
	
		GenericTopLevel gtl=document.createGenericTopLevel("Repository", new QName(annoNS, "SynBioRepository"));
		gtl.createAnnotation(new QName(annoNS, "property1"), "value1");
		gtl.createAnnotation(new QName(annoNS, "property2"), true);
		gtl.createAnnotation(new QName(annoNS, "property3"), 3);
    gtl.createAnnotation(new QName(annoNS, "property10"), 3.3);
    
		
		Annotation childAnnotation1= new Annotation(new QName(annoNS, "property5"), "value5");		
		Annotation childAnnotation2= new Annotation(new QName(annoNS, "property6"), "value6");		
		
		List<Annotation> annotations=List.of(childAnnotation1, childAnnotation2);
		
		Annotation nested1=gtl.createAnnotation(new QName(annoNS, "property4"), new QName(annoNS, "NestedType1"), "nestedid", annotations);
		
		Annotation childAnnotation3= new Annotation(new QName(annoNS, "property8"), "Nested2_value7");		
		Annotation childAnnotation4= new Annotation(new QName(annoNS, "property9"), "Nested2_value8");		
		List<Annotation> nested2_annotations=List.of(childAnnotation3, childAnnotation4);
		Annotation nested2=nested1.createAnnotation(new QName(annoNS, "property7"), new QName(annoNS, "NestedType2"), "nestedid2", nested2_annotations);
	
		SBOLWriter.write(document, System.out);
		
		List<String> errors=TestUtil.roundTripConvert(document);
		TestUtil.DisplayErrors(errors);
		/* 
		String namespaceURI = "http://example.org/ns";
        String localPart = "elementName";

        // Load the QName class without importing it
        Class<?> qnameClass = Class.forName("javax.xml.namespace.QName");

        // Get the constructor that takes (String namespaceURI, String localPart)
        Constructor<?> constructor = qnameClass.getConstructor(String.class, String.class);

        // Create an instance
        Object qnameInstance = constructor.newInstance(namespaceURI, localPart);

        // Optional: print it or cast if needed
        System.out.println("QName via reflection: " + qnameInstance.toString());
        */
	}

}

/*
 * 
 * <?xml version="1.0" ?>
<rdf:RDF xmlns="http://keele.ac.uk/" xmlns:pr="http://partsregistry.org/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:sbol="http://sbols.org/v2#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:prov="http://www.w3.org/ns/prov#" xmlns:om="http://www.ontology-of-units-of-measure.org/resource/om-2/">
  <SynBioRepository rdf:about="http://partsregistry.org/gen/Repository">
    <sbol:persistentIdentity rdf:resource="http://partsregistry.org/gen/Repository"/>
    <sbol:displayId>Repository</sbol:displayId>
    <property1>value1</property1>
    <property2>true</property2>
    <property3>3</property3>
    <property4>
      <NestedType1 rdf:about="http://partsregistry.org/gen/Repository/anno/nestedid">
        <property5>value5</property5>
        <property6>value6</property6>
        <property7>
          <NestedType2 rdf:about="http://partsregistry.org/gen/Repository/anno/nestedid/nestedid2">
            <property8>Nested2_value7</property8>
            <property9>Nested2_value8</property9>
          </NestedType2>
        </property7>
      </NestedType1>
    </property4>
  </SynBioRepository>
</rdf:RDF>
 */