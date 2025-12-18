package org.sbolstandard.converter.sbol31_23;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.OrientationType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

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
			Identified sbol3Parent, Location sbol3SourceLocation)
			throws SBOLGraphException, SBOLValidationException {	

			org.sbolstandard.core2.Component sbol2ParentComponent = (org.sbolstandard.core2.Component) sbol2Parent;
			SubComponent sbol3ParentSubComponent = (SubComponent) sbol3Parent;

			org.sbolstandard.core2.Location sbol2Loc = null;

			Orientation orientation = sbol3SourceLocation.getOrientation();
			OrientationType orientationType = null;
			if (orientation != null) {
				orientationType = Util.toSBOL2OrientationType(orientation);
			}
			// Range
			// if (sbol3SourceLocation instanceof Range) {
			// 		Range sbol3Range = (Range) sbol3SourceLocation;
			// 		if (sbol3SourceLocation.getDisplayId() != null) {
			// 			sbol2Loc = sbol2ParentComponent.addSourceRange(sbol3SourceLocation.getDisplayId(),  sbol3Range.getStart().get(), sbol3Range.getEnd().get(), orientationType);
						
			// 		} else {
			// 			sbol2Loc = sbol2ParentComponent.addSourceRange(sbol3Range.getStart().get(), sbol3Range.getEnd().get(), orientationType);
			// 		}
			// 	}
			





		return null; // TODO: Implement SourceLocation conversion from SBOL3 to SBOL2
	}
}
