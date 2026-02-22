package org.sbolstandard.converter.sbol31_23;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.provenance.ActivityConverter;
import org.sbolstandard.converter.sbol31_23.provenance.AgentConverter;
import org.sbolstandard.converter.sbol31_23.provenance.PlanConverter;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Plan;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.TopLevelMetadata;
import org.sbolstandard.core3.entity.Model;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Attachment;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Experiment;
import org.sbolstandard.core3.entity.ExperimentalData;
import org.sbolstandard.core3.entity.Implementation;

public class SBOLDocumentConverter {

	 static {
        Logger.getLogger("org.hibernate.validator")
              .setLevel(Level.SEVERE);
    }
	 
	public org.sbolstandard.core2.SBOLDocument convert(SBOLDocument sbol3Doc)
			throws SBOLGraphException, SBOLValidationException {
		org.sbolstandard.core2.SBOLDocument sbol2Doc = new org.sbolstandard.core2.SBOLDocument();

		Util.copyNamespacesFrom3_to_2(sbol3Doc, sbol2Doc);
        		
		SequenceConverter seqConverter = new SequenceConverter();
		if (sbol3Doc.getSequences() != null) {
			for (Sequence seq : sbol3Doc.getSequences()) {
				seqConverter.convert(sbol2Doc, seq);
			}
		}

		if (sbol3Doc.getComponents() != null) {
			ComponentConverter comConverter = new ComponentConverter();
			ComponentToModuleDefinitionConverter comToModuleConverter = new ComponentToModuleDefinitionConverter();

			for (Component c : sbol3Doc.getComponents()) {
				if (Util.isModuleDefinition(c)) {
					comToModuleConverter.convert(sbol2Doc, c);
				} 
				else {
					comConverter.convert(sbol2Doc, c);
				}
			}
		}
		if (sbol3Doc.getModels() != null) {
			ModelConverter mConverter = new ModelConverter();
			for (Model m : sbol3Doc.getModels()) {
				mConverter.convert(sbol2Doc, m);
			}
		}

		if (sbol3Doc.getExperimentalData() != null) {
			ExperimentalDataConverter edConverter = new ExperimentalDataConverter();
			for (ExperimentalData ed : sbol3Doc.getExperimentalData()) {
				edConverter.convert(sbol2Doc, ed);
			}
		}

		if (sbol3Doc.getExperiments() != null) {
			ExperimentConverter eConverter = new ExperimentConverter();
			for (Experiment cd : sbol3Doc.getExperiments()) {
				eConverter.convert(sbol2Doc, cd);
			}
		}

		if (sbol3Doc.getCollections() != null) {
			CollectionConverter colConverter = new CollectionConverter();
			for (Collection col : sbol3Doc.getCollections()) {
				colConverter.convert(sbol2Doc, col);
			}
		}

		if (sbol3Doc.getAttachments() != null) {
			AttachmentConverter attConverter = new AttachmentConverter();
			for (Attachment col : sbol3Doc.getAttachments()) {
				attConverter.convert(sbol2Doc, col);
			}
		}

		if (sbol3Doc.getImplementations() != null) {
			ImplementationConverter implConverter = new ImplementationConverter();
			for (Implementation impl : sbol3Doc.getImplementations()) {
				implConverter.convert(sbol2Doc, impl);
			}
		}

		// Mapsto conversion
		if (sbol3Doc.getComponents()!=null){
			for (Component component : sbol3Doc.getComponents()) {
				if (component.getComponentReferences() != null && !component.getComponentReferences().isEmpty()) {
					// WE NEED TO CHECK IF THIS IS A MODULE DEFINITION OR COMPONENT DEFINITION
					if (Util.isModuleDefinition(component)) {
						// Module Definition
						ModuleDefinition moduleDefinition = sbol2Doc.getModuleDefinition(Util.createSBOL2Uri(component.getUri()));
						MapsToMainConverter.convertForModuleDefinition(sbol2Doc, component, moduleDefinition);
						
					} else {
						// Component Definition 
						ComponentDefinition componentDefinition = sbol2Doc.getComponentDefinition(Util.createSBOL2Uri(component.getUri()));
						MapsToMainConverter.convertForComponentDefinition(sbol2Doc, component, componentDefinition);
					}			
				}	
			}
		}

		if (sbol3Doc.getTopLevelMetadataList() != null) {
			TopLevelMetaDataConverter tlmConverter = new TopLevelMetaDataConverter();
			for (TopLevelMetadata tlm : sbol3Doc.getTopLevelMetadataList()) {
				tlmConverter.convert(sbol2Doc, tlm);
			}
		}

		if(sbol3Doc.getPlans()!=null) {
			PlanConverter planConverter = new PlanConverter();
			for(org.sbolstandard.core3.entity.provenance.Plan plan : sbol3Doc.getPlans()) {
				planConverter.convert(sbol2Doc, plan);
			}
		}

		if (sbol3Doc.getAgents() != null) {
			AgentConverter agentConverter = new AgentConverter();
			for (org.sbolstandard.core3.entity.provenance.Agent agent : sbol3Doc.getAgents()) {
				agentConverter.convert(sbol2Doc, agent);
			}
		}

		if (sbol3Doc.getActivities() != null) {
			ActivityConverter activityConverter = new ActivityConverter();
			for (org.sbolstandard.core3.entity.provenance.Activity activity : sbol3Doc.getActivities()) {
				activityConverter.convert(sbol2Doc, activity);
			}
		}

		if (sbol3Doc.getCombinatorialDerivations() != null) {
			CombinatorialDerivationConverter combinatorialDerivationConverter = new CombinatorialDerivationConverter();
			for (org.sbolstandard.core3.entity.CombinatorialDerivation combinatorialDerivation : sbol3Doc.getCombinatorialDerivations()) {
				combinatorialDerivationConverter.convert(sbol2Doc, combinatorialDerivation);
			}
		}
		
		return sbol2Doc;
	}
}
