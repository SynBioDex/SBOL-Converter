package org.sbolstandard.converter.sbol23_31;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RiotException;
import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.provenance.ActivityConverter;
import org.sbolstandard.converter.sbol23_31.provenance.AgentConverter;
import org.sbolstandard.converter.sbol23_31.provenance.PlanConverter;
import org.sbolstandard.core2.Attachment;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Experiment;
import org.sbolstandard.core2.ExperimentalData;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.Implementation;
import org.sbolstandard.core2.Model;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.FeatureWithLocation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.RDFUtil;
import org.sbolstandard.core3.util.SBOLGraphException;


public class SBOLDocumentConverter {

    /* 
	private static boolean isCompliant=false;
	
	public static boolean isCompliantGM() {
		return isCompliant;
	}
    */
	
    private org.apache.jena.rdf.model.Model getRdfModel(String sbol2String, String errorUri) throws RiotException, FileNotFoundException {
        
        try{
            return RDFUtil.read(sbol2String, RDFFormat.RDFXML);
        }
        catch (RiotException e)
        {
            int start =  e.getMessage().indexOf("<");
            int end =  e.getMessage().indexOf(">");
            String uri=e.getMessage().substring(start+1, end);
            if (errorUri==null || (errorUri!=null && !uri.equals(errorUri))) {               
                String newUri= SBOLAPI.append("https://sbolstandard.org/SBOL3-Converter", uri).toString();
                sbol2String=sbol2String.replace("\"" + uri +  "\"", "\"" + newUri +  "\"");
                return getRdfModel(sbol2String, uri);
            }
            else {
                throw e;
            }
        }   
    }


    
    
	public SBOLDocument convert(org.sbolstandard.core2.SBOLDocument sbol2Doc) throws SBOLGraphException, SBOLValidationException, SBOLConversionException, IOException {
        SBOLDocument sbol3Doc = new SBOLDocument();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SBOLWriter.write(sbol2Doc, baos, "RDF");
        String sbol2String = baos.toString();
        //org.apache.jena.rdf.model.Model rdfModel=RDFUtil.read(sbol2String, RDFFormat.RDFXML);
        org.apache.jena.rdf.model.Model rdfModel=getRdfModel(sbol2String, null);

        /*try{
            org.apache.jena.rdf.model.Model rdfModel=RDFUtil.read(sbol2String, RDFFormat.RDFXML);
        }
        catch (RiotException e)
        {}*/

        Parameters parameters=new Parameters();
        parameters.setRdfModel(rdfModel);
        SBOLValidate.validateSBOL(sbol2Doc, false, true, true); 
        //isCompliant=SBOLValidate.getNumErrors()==0;
                
        SequenceConverter seqConverter = new SequenceConverter();
        for (Sequence seq : sbol2Doc.getSequences()) {
        	seqConverter.convert(sbol3Doc, seq, parameters);
        }
        
        ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();
        for (ComponentDefinition cd : sbol2Doc.getComponentDefinitions()) {
            cdConverter.convert(sbol3Doc, cd, parameters);            
        }
        // Convert SequenceAnnotations' locations to SubComponents.locations or SequenceFeatures.locations
        convertLocations(sbol2Doc, sbol3Doc, parameters);
        
        ModelConverter mConverter = new ModelConverter();
        for (Model mod : sbol2Doc.getModels()) {
        	mConverter.convert(sbol3Doc, mod, parameters);
        }

        ModuleDefinitionToComponentConverter mdConverter = new ModuleDefinitionToComponentConverter();
        for (ModuleDefinition md : sbol2Doc.getModuleDefinitions()) {
            mdConverter.convert(sbol3Doc, md, parameters);            
        }                

        ExperimentalDataConverter edConverter = new ExperimentalDataConverter();
        for (ExperimentalData ed : sbol2Doc.getExperimentalData()) {
        	edConverter.convert(sbol3Doc, ed, parameters);            
        }
        
        ExperimentConverter eConverter = new ExperimentConverter();
        for (Experiment expt : sbol2Doc.getExperiments()) {
            eConverter.convert(sbol3Doc, expt,parameters);            
        }
        
        CollectionConverter colConverter = new CollectionConverter();
        for (Collection col : sbol2Doc.getCollections()) {
            colConverter.convert(sbol3Doc, col,parameters);            
        }
        
        AttachmentConverter attConverter = new AttachmentConverter();
        for (Attachment att : sbol2Doc.getAttachments()) {
        	attConverter.convert(sbol3Doc, att,parameters);            
        }
        
        ImplementationConverter implConverter = new ImplementationConverter();
        for (Implementation impl : sbol2Doc.getImplementations()) {
        	implConverter.convert(sbol3Doc, impl,parameters);            
        }
        
        GenericTopLevelConverter gtlConverter = new GenericTopLevelConverter();
        for (GenericTopLevel gtl : sbol2Doc.getGenericTopLevels()) {
        	gtlConverter.convert(sbol3Doc, gtl,parameters);            
        }
       
        // MapsTo conversion from Component Definition
        for (ComponentDefinition sbol2CD : sbol2Doc.getComponentDefinitions()) {
			// For each ComponentDefinition, look for MapsTo
			MapstoToMainConverter.convertFromComponentDefinition(sbol3Doc, sbol2CD,parameters);
		}
       
        // MapsTo conversion from Module Definition
        for (ModuleDefinition sbol2MD : sbol2Doc.getModuleDefinitions()) {
			// For each ComponentDefinition, look for MapsTo
			MapstoToMainConverter.convertFromModuleDefinition(sbol3Doc, sbol2MD,parameters);
		}
        
        PlanConverter planConverter = new PlanConverter();
        for (org.sbolstandard.core2.Plan sbol2Plan : sbol2Doc.getPlans()) {
            planConverter.convert(sbol3Doc, sbol2Plan, parameters);            
        }

        AgentConverter agentConverter = new AgentConverter();
        for (org.sbolstandard.core2.Agent agent : sbol2Doc.getAgents()) {
            agentConverter.convert(sbol3Doc, agent, parameters);            
        }

        // Activity conversion
        ActivityConverter activityConverter = new ActivityConverter();
        for (org.sbolstandard.core2.Activity sbol2Activity : sbol2Doc.getActivities()) {
            activityConverter.convert(sbol3Doc, sbol2Activity, parameters);
        }


        Util.copyNamespacesFrom2_to_3(sbol2Doc, sbol3Doc);
        
        return sbol3Doc;
	}

    private void convertLocations(org.sbolstandard.core2.SBOLDocument sbol2Doc, SBOLDocument document, Parameters parameters) throws SBOLGraphException {
        ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();
        for (ComponentDefinition cd : sbol2Doc.getComponentDefinitions()) {
            for (SequenceAnnotation sbol2SeqAnno : cd.getSequenceAnnotations()) {
                org.sbolstandard.core3.entity.Sequence sbol3Sequence = Util.getSBOL3SequenceFromSBOl2Parent(document, cd, parameters);

                URI sbol3ComponentURI = Util.createSBOL3Uri(cd);
                Component sbol3ParentComp = document.getIdentified(sbol3ComponentURI, Component.class);
                if (sbol3Sequence == null) {
                    sbol3Sequence = Util.getEmptySequence(sbol3ParentComp, document);
                }
                SubComponent sbol3SubComponent = null;
                SequenceFeature sbol3SequenceFeature = null;                
                FeatureWithLocation featureWithLocation=null;

                if (sbol2SeqAnno.isSetComponent()) {
                    URI sbol3SubComponentURI = parameters.getMapping(sbol2SeqAnno.getComponent().getIdentity());
                    sbol3SubComponent = document.getIdentified(sbol3SubComponentURI, SubComponent.class);
                    featureWithLocation=sbol3SubComponent;
                } 
                else {
                    URI sbol3SequenceFeatureURI = parameters.getMapping(sbol2SeqAnno.getIdentity());
                    sbol3SequenceFeature = document.getIdentified(sbol3SequenceFeatureURI, SequenceFeature.class);
                     featureWithLocation=sbol3SequenceFeature;
                }
                
                if (sbol2SeqAnno.getLocations() != null) {
                    for (org.sbolstandard.core2.Location sbol2Location : sbol2SeqAnno.getLocations()) {
                        org.sbolstandard.core3.entity.Sequence sbol3LocationSequence=null;
                        if (sbol2Location.getSequenceURI()!=null){
                            URI sbol3LocationSequenceURI = Util.createSBOL3Uri(sbol2Location.getSequenceURI(), parameters);
                            sbol3LocationSequence = document.getIdentified(sbol3LocationSequenceURI, org.sbolstandard.core3.entity.Sequence.class);
                        }
                        if (sbol3LocationSequence == null) {
                            sbol3LocationSequence = sbol3Sequence;
                        }
                        if (sbol3LocationSequence == null) {
                            org.sbolstandard.core2.Component sbol2Comp = sbol2SeqAnno.getComponent();
                            org.sbolstandard.core2.ComponentDefinition sbol2CompDef = sbol2Comp.getDefinition();
                            org.sbolstandard.core2.Sequence sbol2ChildSequence = sbol2CompDef.getSequences().iterator().next();
                            if (sbol2ChildSequence == null) {
                                sbol3LocationSequence = Util.getEmptySequence(sbol3ParentComp, document);
                            } else {
                                URI sbol3SubComponentSequenceUri = Util.createSBOL3Uri(sbol2ChildSequence.getIdentity(), parameters);
                                sbol3LocationSequence = document.getIdentified(sbol3SubComponentSequenceUri,org.sbolstandard.core3.entity.Sequence.class);
                            }
                        }

                        Location sbol3Location = null;
                        org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation = Util.toSBOL3Orientation(sbol2Location.getOrientation());

                        // Handle Range location type (start and end positions).
                        if (sbol2Location instanceof org.sbolstandard.core2.Range) {
                            org.sbolstandard.core2.Range sbol2Range = (org.sbolstandard.core2.Range) sbol2Location;
                            if (sbol2Location.getDisplayId()!=null) {	
                                sbol3Location = featureWithLocation.createRange(sbol2Location.getDisplayId(), sbol2Range.getStart(), sbol2Range.getEnd(), sbol3LocationSequence);
                            } 
                            else {
                                sbol3Location = featureWithLocation.createRange(sbol2Range.getStart(), sbol2Range.getEnd(), sbol3LocationSequence);
                            }
                        } 
                        else if (sbol2Location instanceof org.sbolstandard.core2.Cut) {
                            org.sbolstandard.core2.Cut sbol2Cut = (org.sbolstandard.core2.Cut) sbol2Location;
                            if (sbol2Location.getDisplayId()!=null) {	                            
                                sbol3Location = featureWithLocation.createCut(sbol2Location.getDisplayId(), sbol2Cut.getAt(), sbol3LocationSequence);
                            }
                            else {
                                sbol3Location = featureWithLocation.createCut(sbol2Cut.getAt(), sbol3LocationSequence);
                            }
                        } 
                        else if (sbol2Location instanceof org.sbolstandard.core2.GenericLocation) {                          
                            /*if (sbol3LocationSequence==null) {
                                org.sbolstandard.core2.Component sbol2Comp= sbol2SeqAnno.getComponent();
                                org.sbolstandard.core2.ComponentDefinition sbol2CompDef=sbol2Comp.getDefinition();
                                org.sbolstandard.core2.Sequence sbol2ChildSequence = sbol2CompDef.getSequences().iterator().next();
                                if (sbol2ChildSequence==null){
                                    sbol3LocationSequence= Util.getEmptySequence(sbol3ParentComp, document);
                                }
                                else{
                                    URI sbol3SubComponentSequenceUri=Util.createSBOL3Uri(sbol2ChildSequence.getIdentity(), parameters);
					                sbol3LocationSequence = document.getIdentified(sbol3SubComponentSequenceUri, org.sbolstandard.core3.entity.Sequence.class);                                    
                                }
                            }*/

                            if (sbol2Location.getDisplayId()!=null) {	                                                       
                                sbol3Location = featureWithLocation.createEntireSequence(sbol2Location.getDisplayId(), sbol3LocationSequence);
                            }
                            else {
                                sbol3Location = featureWithLocation.createEntireSequence(sbol3LocationSequence);
                            }
                        }

                        sbol3Location.setOrientation(sbol3Orientation);
                        Util.copyIdentified(sbol2Location, sbol3Location, parameters);
                    }
                }
            }

        }
    }

}