package org.sbolstandard.converter.sbol23_31;


import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.RefinementType;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapstoToConstraintConverter {

	public static Constraint convert(Component sbol3ParentComponent, MapsTo mapsTo, ComponentReference sbol3CompRef) throws SBOLGraphException, SBOLValidationException {

		Constraint sbol3Constraint = null;
		SubComponent sbol3LocalSubComponent = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), mapsTo.getLocal());

		RefinementType refinementType = mapsTo.getRefinement();
		if (refinementType.equals(org.sbolstandard.core2.RefinementType.USELOCAL)) {
			System.out.println("REFINEMENT TYPE IS USELOCAL");
			
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces,
					sbol3LocalSubComponent, sbol3CompRef);

		} else if (refinementType.equals(org.sbolstandard.core2.RefinementType.USEREMOTE)) {
			System.out.println("REFINEMENT TYPE IS USEREMOTE");
			
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces, sbol3CompRef,
					sbol3LocalSubComponent);
		} else if (refinementType.equals(org.sbolstandard.core2.RefinementType.VERIFYIDENTICAL)) {
			System.out.println("REFINEMENT TYPE IS VERIFYIDENTICAL");
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces, sbol3CompRef,
					sbol3LocalSubComponent);
		} else if (refinementType.equals(org.sbolstandard.core2.RefinementType.MERGE)) {
			System.out.println("REFINEMENT TYPE IS MERGE");
			// REMOVED IN SBOL3. HANDLED AS USEREMOTE
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces, sbol3CompRef,
					sbol3LocalSubComponent);
		} else {
			throw new SBOLGraphException("Unknown refinement type: " + refinementType);
		}

		return sbol3Constraint;

	}
	

}
