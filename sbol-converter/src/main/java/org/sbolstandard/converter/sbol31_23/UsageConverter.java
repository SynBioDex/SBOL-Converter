package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.provenance.Usage;

public class UsageConverter implements ChildEntityConverter<Usage, org.sbolstandard.core2.Usage>  {

	@Override
	public org.sbolstandard.core2.Usage convert(SBOLDocument document, Identified parent, org.sbolstandard.core3.entity.Identified inputParent, Usage sbol3Usage) throws SBOLGraphException, SBOLValidationException {

		Activity sbol2ParentActivity = (Activity) parent;
		
		org.sbolstandard.core2.Usage sbol2Usage = sbol2ParentActivity.createUsage(sbol3Usage.getDisplayId() , Util.createSBOL2Uri(sbol3Usage.getEntity()));

		sbol2Usage.setRoles(Util.toSet(sbol3Usage.getRoles()));

		Util.copyIdentified(sbol3Usage, sbol2Usage, document);

		return sbol2Usage;

	}

}