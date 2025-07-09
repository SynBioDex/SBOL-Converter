package org.sbolstandard.converter.sbol23_31;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.jena.riot.RDFFormat;
import org.sbolstandard.converter.Util;
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
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.RDFUtil;
import org.sbolstandard.core3.util.SBOLGraphException;


public class SBOLDocumentConverter {

    /* 
	private static boolean isCompliant=false;
	
	public static boolean isCompliantGM() {
		return isCompliant;
	}
    */
	
	public SBOLDocument convert(org.sbolstandard.core2.SBOLDocument sbol2Doc) throws SBOLGraphException, SBOLValidationException, SBOLConversionException, IOException {
        SBOLDocument sbol3Doc = new SBOLDocument();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SBOLWriter.write(sbol2Doc, baos, "RDF");
        String sbol2String = baos.toString();
        org.apache.jena.rdf.model.Model rdfModel=RDFUtil.read(sbol2String, RDFFormat.RDFXML);
        

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
        
        Util.copyNamespacesFrom2_to_3(sbol2Doc, sbol3Doc);
        
        return sbol3Doc;
	}
}
