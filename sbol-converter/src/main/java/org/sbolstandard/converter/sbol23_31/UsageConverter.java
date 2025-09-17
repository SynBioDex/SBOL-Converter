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
		if (sbol2Usage.getEntityURI() == null) {
			throw new SBOLGraphException("Usage entity is null: " + sbol2Usage.getDisplayId());
		}
		System.out.println("Usage Display ID: " + sbol2Usage.getDisplayId());

		// TODO: Entity CONVERTER
	

		//Usage sbol3Usage = sbol3ParentActivity.createUsage(Util.createSBOL3Uri(sbol2Usage), Util.createSBOL3Uri(sbol2Usage.getEntityURI(),parameters));
		Usage sbol3Usage = null;
		if (sbol2Usage.getDisplayId()!=null){
			sbol3Usage = sbol3ParentActivity.createUsage(sbol2Usage.getDisplayId(), Util.createSBOL3Uri(sbol2Usage.getEntityURI(),parameters));
		}
		else{
			sbol3Usage = sbol3ParentActivity.createUsage(Util.createSBOL3Uri(sbol2Usage.getEntityURI(),parameters));
			parameters.addMapping(sbol2Usage.getIdentity(), sbol3Usage.getUri());
		}
		sbol3Usage.setRoles(Util.toList(sbol2Usage.getRoles()));

		Util.copyIdentified(sbol2Usage, sbol3Usage, parameters);

		return sbol3Usage;
	}

}