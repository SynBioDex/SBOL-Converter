package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Sequence;

import java.net.URI;


import org.sbolstandard.converter.Util;

public class SequenceAnnotationToFeatureConverter implements ChildEntityConverter<org.sbolstandard.core2.SequenceAnnotation,SequenceFeature>{

	@Override
	public SequenceFeature convert(SBOLDocument document, Identified parent, SequenceAnnotation seqa)
			throws SBOLGraphException {
		
		// SBOL 2 SequenceAnnotation maps to SBOL3 SequenceFeature
		
		Component parentComponent = (Component) parent;
		
		
		//SequenceFeature feature = new SequenceFeature(document.getRDFModel(), seqa.getIdentity());
		
		org.sbolstandard.core2.Sequence sbol2seq = seqa.getComponentDefinition().getSequenceByEncoding(Util.getSBOL2SequenceType(seqa.getComponentDefinition()));
		
		URI SBOL3ObjectUri = Util.createSBOL3Uri(sbol2seq.getIdentity());
		
		Sequence sbol3seq = document.getIdentified(SBOL3ObjectUri, Sequence.class);
		
		SequenceFeature seqFeature = null;
	
		
		for (org.sbolstandard.core2.Location sbol2Loc: seqa.getLocations()) {
			org.sbolstandard.core2.OrientationType sbol2Orinentation = sbol2Loc.getOrientation();
			org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation = Util.toSBOL3Orientation(sbol2Orinentation);
			
			
			if(sbol2Loc instanceof org.sbolstandard.core2.Range) {
				org.sbolstandard.core2.Range sbol2range = (org.sbolstandard.core2.Range) sbol2Loc;
				if(seqFeature == null) {
					seqFeature = parentComponent.createSequenceFeature(sbol2range.getStart(), sbol2range.getEnd(), sbol3seq);
					seqFeature.setOrientation(null);
				}else {
					seqFeature.createRange(sbol2range.getStart(), sbol2range.getEnd(), sbol3seq);
				}
			}else if(sbol2Loc instanceof org.sbolstandard.core2.Cut) {
				org.sbolstandard.core2.Cut sbol2cut = (org.sbolstandard.core2.Cut) sbol2Loc;
				if(seqFeature == null) {
					seqFeature = parentComponent.createSequenceFeature(sbol2cut.getAt(), sbol3seq);
				}else {
					seqFeature.createCut(sbol2cut.getAt(), sbol3seq);
				}
				
			}else if(sbol2Loc instanceof org.sbolstandard.core2.GenericLocation) {
				//org.sbolstandard.core2.GenericLocation sbol2genLoc = (org.sbolstandard.core2.GenericLocation) sbol2Loc;
				if(seqFeature == null) {
					seqFeature = parentComponent.createSequenceFeature(sbol3seq);
				}else {
					seqFeature.createEntireSequence(sbol3seq);
				}
			}
			if(seqFeature != null) {
				seqFeature.setOrientation(sbol3Orientation);
			}
			
		}
		
		Util.copyIdentified(seqa, seqFeature);
		
		return seqFeature;
	}

}
