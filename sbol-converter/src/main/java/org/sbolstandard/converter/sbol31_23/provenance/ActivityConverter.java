package org.sbolstandard.converter.sbol31_23.provenance;



import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.joda.time.DateTime;
import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.EntityConverter;
import org.sbolstandard.core3.entity.provenance.Activity;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

public class ActivityConverter implements EntityConverter<Activity, org.sbolstandard.core2.Activity>  {

    @Override
    public  org.sbolstandard.core2.Activity convert(SBOLDocument doc, Activity input) throws SBOLGraphException, SBOLValidationException {      	
    	
		// Create activity
		org.sbolstandard.core2.Activity activity = doc.createActivity(Util.getURIPrefix(input), input.getDisplayId(), Util.getVersion(input));
		if(input.getTypes()!=null) {
			activity.setTypes(Util.toSet(input.getTypes()));
		}

		if(input.getStartedAtTime()!=null) {
			activity.setStartedAtTime(convertToDateTime(input.getStartedAtTime()));
		}
		if(input.getEndedAtTime()!=null) {
			activity.setEndedAtTime(convertToDateTime(input.getEndedAtTime()));
		}

		// Associations and usages must be converted but in here as the child entities of activity
		
		if (input.getAssociations() != null) {
			AssociationConverter associationConverter = new AssociationConverter();
			for (org.sbolstandard.core3.entity.provenance.Association assoc : input.getAssociations()) {
				associationConverter.convert(doc, activity, input, assoc);
			}	
			
		}
		if (input.getUsages() != null) {
			UsageConverter usageConverter = new UsageConverter();
			for (org.sbolstandard.core3.entity.provenance.Usage usage : input.getUsages()) {
				usageConverter.convert(doc, activity, input, usage);
			}	
			
		}

		Util.copyIdentified(input, activity, doc);
		return activity;


	}

	private static DateTime convertToDateTime(XSDDateTime xsdDateTime) {
		if (xsdDateTime == null) return null;
		
		// Convert XSDDateTime to java.util.Calendar
        java.util.Calendar calendar = xsdDateTime.asCalendar();
        // Wrap calendar in Joda DateTime
        return new DateTime(calendar);
		
	}
}
