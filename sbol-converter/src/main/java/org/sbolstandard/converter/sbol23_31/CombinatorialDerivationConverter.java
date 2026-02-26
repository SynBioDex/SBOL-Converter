package org.sbolstandard.converter.sbol23_31;

import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.OperatorType;
import org.sbolstandard.core2.StrategyType;
import org.sbolstandard.core2.VariableComponent;
import org.sbolstandard.core3.entity.CombinatorialDerivation;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.VariableFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.SBOLUtil;
import org.sbolstandard.core3.vocabulary.CombinatorialDerivationStrategy;
import org.sbolstandard.core3.vocabulary.VariableFeatureCardinality;

public class CombinatorialDerivationConverter implements EntityConverter<org.sbolstandard.core2.CombinatorialDerivation, CombinatorialDerivation>  {

    @Override
    public CombinatorialDerivation convert(SBOLDocument doc, org.sbolstandard.core2.CombinatorialDerivation input, Parameters parameters) throws SBOLGraphException { 
    	URI sbol3TemplateURI=Util.createSBOL3Uri(input.getTemplateURI(), parameters);
		CombinatorialDerivation cd = doc.createCombinatorialDerivation(Util.createSBOL3Uri(input), Util.getNamespace(input), sbol3TemplateURI);
		
		Util.copyIdentified(input, cd, parameters);
		if (input.isSetStrategy()) {
			cd.setStrategy(convertStrategy23_31(input.getStrategy()));
		}
		for (VariableComponent vComponent : input.getVariableComponents()) {
			convertVariableComponent(cd, vComponent, parameters);
		}
		return cd;
	}
    
    /**
     * Converts strategy VariableComponent from SBOL2 to VariableFeature from SBOL3 and add it to given CombinatorialDerivation
     * @throws SBOLGraphException
     */
    private VariableFeature convertVariableComponent(CombinatorialDerivation cDerivation, VariableComponent vComponent, Parameters parameters)  throws SBOLGraphException
    {
    	if (cDerivation.getTemplate()==null)
		{
			Util.getLogger().error("*****CONVERSION ERROR:*****: Template is not set for CombinatorialDerivation " + cDerivation.getUri() +  ". Can't proceed to create the VariableFeature.");
			return null;
		}
		List<Feature> sbol3Features = cDerivation.getTemplate().getFeatures();
		URI sbol3FeatureURI=Util.createSBOL3Uri(vComponent.getVariableURI(), parameters);
		Feature foundFeature=null;
		for (Feature feature : sbol3Features){
			if (feature.getUri().equals(sbol3FeatureURI)){
				foundFeature=feature;
				break;
			}
		}

		VariableFeature vFeature=null;
		if (foundFeature!=null){
			vFeature  = cDerivation.createVariableFeature(Util.createSBOL3Uri(vComponent), convertOperatorType(vComponent.getOperator()), foundFeature ); 
			
			//Set variant collections
			List<URI> variantCollectionURIs = new ArrayList<>();
			for (URI collectionURI : vComponent.getVariantCollectionURIs()) {
				variantCollectionURIs.add(Util.createSBOL3Uri(collectionURI, parameters));		
			}	
			if (variantCollectionURIs.size()>0){
				vFeature.setVariantCollectionURIs(variantCollectionURIs);
			}

			//Set variant derivations
			List<URI> variantDerivations = new ArrayList<>();
			for (URI cdURI :vComponent.getVariantDerivationURIs()) {
				variantDerivations.add(Util.createSBOL3Uri(cdURI, parameters));
			}
			if (variantDerivations.size()>0){
				vFeature.setVariantDerivationURIs(variantDerivations);
			}

			//Set variants
			List<URI> variants = new ArrayList<>();
			for (URI variantURI : vComponent.getVariantURIs()) {
				variants.add(Util.createSBOL3Uri(variantURI, parameters));
			}
			if (variants.size()>0){			
				vFeature.setVariantURIs(variants);
			}    	
		}
		return vFeature;
    }
    
    /**
     * Converts strategy StrategyType from SBOL2 to CombinatorialDerivationStrategy from SBOL3
     * @throws SBOLGraphException
     */
	private CombinatorialDerivationStrategy convertStrategy23_31(StrategyType strategy2) throws SBOLGraphException {
		switch (strategy2) {
		case ENUMERATE: {
			return CombinatorialDerivationStrategy.Enumerate;
		}
		case SAMPLE: {
			return CombinatorialDerivationStrategy.Sample;
		}
		default: {
			throw new SBOLGraphException("Unknown strategy type " + strategy2.toString());
		}
		}
	}
	
	   /**
     * Converts strategy OperatorType from SBOL2 to VariableFeatureCardinality from SBOL3
     * @throws SBOLGraphException
     */
		private static VariableFeatureCardinality convertOperatorType(OperatorType operatorType) throws SBOLGraphException {
			switch (operatorType) {
			case ZEROORONE: {
				return VariableFeatureCardinality.ZeroOrOne;
			}
			case ONE: {
				return VariableFeatureCardinality.One;
			}
			case ONEORMORE: {
				return VariableFeatureCardinality.OneOrMore;
			}
			case ZEROORMORE: {
				return VariableFeatureCardinality.ZeroOrMore;
			}
			default: {
				throw new SBOLGraphException("Unknown operatorType " + operatorType.toString());
			}
			}
		}
}
