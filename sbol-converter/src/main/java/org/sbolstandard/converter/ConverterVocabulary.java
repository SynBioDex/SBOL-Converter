package org.sbolstandard.converter;

import java.net.URI;

public class ConverterVocabulary {

    public static final class Two_to_Three {
        //public static final String sbol2BackPortTermPrefix = "sbol2";
        public static final URI sbol2SequenceAnnotationURI = ConverterNameSpace.BackPort_2_3.local("sbol2OriginalSequenceAnnotationURI");
        public static final URI sbol3TempSequenceURI = ConverterNameSpace.BackPort_2_3.local("sbol3TempSequenceURI");
        public static final URI sbol2LocationSequenceNull = ConverterNameSpace.BackPort_2_3.local("sbol2LocationSequenceNull");
        
        
    }
		
}
