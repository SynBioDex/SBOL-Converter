package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

public class Parameters {
	private Map<URI,URI> mappings=new HashMap<URI, URI>();
	private Model model=null;
	
	public void addMapping(URI sbol2, URI sbol3)	{
		mappings.put(sbol2, sbol3);
	}
	
	public URI getMapping(URI sbol2)	{
		return mappings.get(sbol2);
	}

	public void setRdfModel(Model rdfModel)	{
		this.model=rdfModel;
	}
	public Model getRdfModel()	{
		return model;
	}

	

}
