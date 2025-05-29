package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Location;

public class LocationConverter implements ChildEntityConverter<org.sbolstandard.core2.Location,Location>  {

    @Override
    public Location convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Location sbol2loc) throws SBOLGraphException {
		
    	return null;
    }
}
