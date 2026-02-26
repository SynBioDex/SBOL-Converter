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

	public static Constraint convert(Component sbol3ParentComponent, MapsTo mapsTo, ComponentReference sbol3CompRef, Parameters parameters) throws SBOLGraphException, SBOLValidationException {

		Constraint sbol3Constraint = null;
		SubComponent sbol3LocalSubComponent = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), mapsTo.getLocal(), parameters);

		RefinementType refinementType = mapsTo.getRefinement();
		if (refinementType.equals(org.sbolstandard.core2.RefinementType.USELOCAL)) {
			Util.getLogger().debug("Refinement type is USELOCAL");
			
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces,
					sbol3LocalSubComponent, sbol3CompRef);

		} else if (refinementType.equals(org.sbolstandard.core2.RefinementType.USEREMOTE)) {
			Util.getLogger().debug("Refinement type is USEREMOTE");
			
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces, sbol3CompRef,
					sbol3LocalSubComponent);
		} else if (refinementType.equals(org.sbolstandard.core2.RefinementType.VERIFYIDENTICAL)) {
			Util.getLogger().debug("Refinement type is VERIFYIDENTICAL");
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.verifyIdentical, sbol3CompRef,
					sbol3LocalSubComponent);
		} else if (refinementType.equals(org.sbolstandard.core2.RefinementType.MERGE)) {
			Util.getLogger().debug("Refinement type is MERGE");
			// Removed in SBOL3 and handled as USEREMOTE
			sbol3Constraint = sbol3ParentComponent.createConstraint(
					org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces, sbol3CompRef,
					sbol3LocalSubComponent);
		} else {
			throw new SBOLGraphException("Unknown refinement type: " + refinementType);
		}
		return sbol3Constraint;
	}
}
