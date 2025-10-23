package org.sbolstandard.converter.sbol23_31.provenance;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.EntityConverter;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core3.entity.provenance.Activity;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ActivityConverter implements EntityConverter<org.sbolstandard.core2.Activity, Activity> {

	@Override
	public Activity convert(SBOLDocument doc, org.sbolstandard.core2.Activity input, Parameters parameters) throws SBOLGraphException {
		
		Activity activity = doc.createActivity(Util.createSBOL3Uri(input), Util.getNamespace(input));

		if(input.getTypes() != null){
			activity.setTypes(Util.toList(input.getTypes()));
		}

		if (input.getStartedAtTime() != null) {
			activity.setStartedAtTime (input.getStartedAtTime().getYear(), input.getStartedAtTime().getMonthOfYear(), input.getStartedAtTime().getDayOfMonth(),
					input.getStartedAtTime().getHourOfDay(), input.getStartedAtTime().getMinuteOfHour(), input.getStartedAtTime().getSecondOfMinute());
		}
		if (input.getEndedAtTime() != null) {
			activity.setEndedAtTime(input.getEndedAtTime().getYear(), input.getEndedAtTime().getMonthOfYear(), input.getEndedAtTime().getDayOfMonth(),
					input.getEndedAtTime().getHourOfDay(), input.getEndedAtTime().getMinuteOfHour(), input.getEndedAtTime().getSecondOfMinute());
		}

		// Associations and usages must be converted but in here as the child entities of activity
		AssociationConverter associationConverter = new AssociationConverter();
		if (input.getAssociations() != null) {
			for (org.sbolstandard.core2.Association assoc : input.getAssociations()) {
				associationConverter.convert(doc, activity, input, assoc, parameters);
			}
		}
		UsageConverter usageConverter = new UsageConverter();
		if (input.getUsages() != null) {
			for (org.sbolstandard.core2.Usage usage : input.getUsages()) {
				usageConverter.convert(doc, activity, input, usage, parameters);
			}
		}

		Util.copyIdentified(input, activity, parameters);
		return activity;
	}
}

