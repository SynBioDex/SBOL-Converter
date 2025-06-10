package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.Attachment;
import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Experiment;
import org.sbolstandard.core2.ExperimentalData;
import org.sbolstandard.core2.Implementation;
import org.sbolstandard.core2.Model;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class SBOLDocumentConverter {

	private static boolean isCompliant=false;
	
	public static boolean isCompliant() {
		return isCompliant;
	}
	
	public SBOLDocument convert(org.sbolstandard.core2.SBOLDocument sbol2Doc) throws SBOLGraphException, SBOLValidationException {
        SBOLDocument sbol3Doc = new SBOLDocument();
        
        SBOLValidate.validateSBOL(sbol2Doc, false, true, true); 
        isCompliant=SBOLValidate.getNumErrors()==0;
        
        SequenceConverter seqConverter = new SequenceConverter();
        for (Sequence seq : sbol2Doc.getSequences()) {
        	seqConverter.convert(sbol3Doc, seq);
        }
        
        ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();
        for (ComponentDefinition cd : sbol2Doc.getComponentDefinitions()) {
            cdConverter.convert(sbol3Doc, cd);            
        }
       
        
        ModelConverter mConverter = new ModelConverter();
        for (Model mod : sbol2Doc.getModels()) {
        	mConverter.convert(sbol3Doc, mod);
        }

        ModuleDefinitionToComponentConverter mdConverter = new ModuleDefinitionToComponentConverter();
        for (ModuleDefinition md : sbol2Doc.getModuleDefinitions()) {
            mdConverter.convert(sbol3Doc, md);            
        }
                
        ExperimentalDataConverter edConverter = new ExperimentalDataConverter();
        for (ExperimentalData ed : sbol2Doc.getExperimentalData()) {
        	edConverter.convert(sbol3Doc, ed);            
        }
        
        ExperimentConverter eConverter = new ExperimentConverter();
        for (Experiment expt : sbol2Doc.getExperiments()) {
            eConverter.convert(sbol3Doc, expt);            
        }
        
        CollectionConverter colConverter = new CollectionConverter();
        for (Collection col : sbol2Doc.getCollections()) {
            colConverter.convert(sbol3Doc, col);            
        }
        
        AttachmentConverter attConverter = new AttachmentConverter();
        for (Attachment att : sbol2Doc.getAttachments()) {
        	attConverter.convert(sbol3Doc, att);            
        }
        
        ImplementationConverter implConverter = new ImplementationConverter();
        for (Implementation impl : sbol2Doc.getImplementations()) {
        	implConverter.convert(sbol3Doc, impl);            
        }
  
        return sbol3Doc;
	}
}
