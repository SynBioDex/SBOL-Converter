package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.OperatorType;
import org.sbolstandard.core2.StrategyType;
import org.sbolstandard.core2.VariableComponent;
import org.sbolstandard.core3.entity.CombinatorialDerivation;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.VariableFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.CombinatorialDerivationStrategy;
import org.sbolstandard.core3.vocabulary.VariableFeatureCardinality;

public class CombinatorialDerivationConverter implements EntityConverter<org.sbolstandard.core2.CombinatorialDerivation, CombinatorialDerivation>  {

    @Override
    public CombinatorialDerivation convert(SBOLDocument doc, org.sbolstandard.core2.CombinatorialDerivation input, Parameters parameters) throws SBOLGraphException { 
    	CombinatorialDerivation cd = doc.createCombinatorialDerivation(Util.createSBOL3Uri(input), Util.getNamespace(input), null ); //TODO: need method with URI instead of Component
		Util.copyIdentified(input, cd);
		if (input.isSetStrategy()) {
			cd.setStrategy(convertStrategy23_31(input.getStrategy()));
		}
		for (VariableComponent vComponent : input.getVariableComponents()) {
			convertVariableComponent(cd, vComponent);
		}
		return cd;
	}
    
    /**
     * Converts strategy VariableComponent from SBOL2 to VariableFeature from SBOL3 and add it to given CombinatorialDerivation
     * @throws SBOLGraphException
     */
    private VariableFeature convertVariableComponent(CombinatorialDerivation cDerivation, VariableComponent vComponent)  throws SBOLGraphException
    {
    	VariableFeature vFeature  = cDerivation.createVariableFeature(Util.createSBOL3Uri(vComponent), convertOperatorType(vComponent.getOperator()), null ); //TODO: can not create Feature
    	List<URI> variantCollections = new ArrayList<>();
    	for (org.sbolstandard.core2.Collection collection : vComponent.getVariantCollections()) {
    		variantCollections.add(Util.createSBOL3Uri(collection));		
    	}
    	vFeature.setVariantCollectionURIs(variantCollections);
    	List<URI> variantDerivations = new ArrayList<>();
		for (org.sbolstandard.core2.CombinatorialDerivation cd :vComponent.getVariantDerivations()) {
			variantDerivations.add(Util.createSBOL3Uri(cd));
		}
		vFeature.setVariantDerivationURIs(variantDerivations);
		List<URI> variants = new ArrayList<>();
		for (org.sbolstandard.core2.ComponentDefinition c: vComponent.getVariants()) {
			variants.add(Util.createSBOL3Uri(c));
		}
		vFeature.setVariantURIs(variants);
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
