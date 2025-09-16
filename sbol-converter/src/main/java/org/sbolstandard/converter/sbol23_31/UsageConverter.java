package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.provenance.Activity;
import org.sbolstandard.core3.entity.provenance.Usage;
import org.sbolstandard.core3.util.SBOLGraphException;

public class UsageConverter implements ChildEntityConverter<org.sbolstandard.core2.Usage, Usage> {

	@Override
	public Usage convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.Usage sbol2Usage, Parameters parameters) throws SBOLGraphException {

		Activity sbol3ParentActivity = (Activity) parent;
		
		System.out.println("Usage Entity: " + sbol2Usage.getEntity());
		System.out.println("Usage Display ID: " + sbol2Usage.getDisplayId());

		// TODO: Entity CONVERTER
	

		Usage sbol3Usage = sbol3ParentActivity.createUsage(Util.createSBOL3Uri(sbol2Usage), Util.createSBOL3Uri(sbol2Usage.getEntity()));

		sbol3Usage.setRoles(Util.toList(sbol2Usage.getRoles()));

		Util.copyIdentified(sbol2Usage, sbol3Usage, parameters);

		return sbol3Usage;
	}

}