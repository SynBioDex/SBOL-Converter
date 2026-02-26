package org.sbolstandard.converter.sbol31_23;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.OrientationType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Cut;
import org.sbolstandard.core3.entity.EntireSequence;
import org.sbolstandard.core3.entity.Identified;

import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.Orientation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Range;
import org.sbolstandard.core3.entity.SubComponent;

// This converter maps an SBOL3 SourceLocation to 
public class SourceLocationConverter
		implements ChildEntityConverter<Location, org.sbolstandard.core2.Location> {

	@Override
	public org.sbolstandard.core2.Location convert(SBOLDocument document, org.sbolstandard.core2.Identified sbol2Parent,
			Identified sbol3Parent, Location loc)
			throws SBOLGraphException, SBOLValidationException {	

			org.sbolstandard.core2.Component sbol2ParentComponent = (org.sbolstandard.core2.Component) sbol2Parent;
			
			org.sbolstandard.core2.Location sbol2Loc = null;

			Orientation orientation = loc.getOrientation();
			OrientationType orientationType = null;
			if (orientation != null) {
				orientationType = Util.toSBOL2OrientationType(orientation);
			}
			
			// Handle Range location (with start and end coordinates)
			if (loc instanceof Range) {
				Range range = (Range) loc;
				sbol2Loc = sbol2ParentComponent.addSourceRange(loc.getDisplayId(), range.getStart().get(),range.getEnd().get(), orientationType);					
			}
			// Handle Cut location (single position)
			else if (loc instanceof Cut) {
				Cut cut = (Cut) loc;
				sbol2Loc = sbol2ParentComponent.addSourceCut(loc.getDisplayId(), cut.getAt().get(), orientationType);					
			}
			// Handle EntireSequence location (refers to the whole sequence)
			else if (loc instanceof EntireSequence) {
				sbol2Loc = sbol2ParentComponent.addGenericSourceLocation(loc.getDisplayId(), orientationType);					
			}			

			if (!Util.isFrom2To3(null)) {
				//TODO: Add the order and hasSequence properties as annotations
			}
		return sbol2Loc;
	}
}
