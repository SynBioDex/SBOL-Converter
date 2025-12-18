package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core3.entity.CombinatorialDerivation;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.VariableFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.CombinatorialDerivationStrategy;
import org.sbolstandard.core3.vocabulary.VariableFeatureCardinality;
import org.sbolstandard.core2.OperatorType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.StrategyType;
import org.sbolstandard.core2.VariableComponent;

public class CombinatorialDerivationConverter implements EntityConverter<CombinatorialDerivation, org.sbolstandard.core2.CombinatorialDerivation>  {

    @Override
	public org.sbolstandard.core2.CombinatorialDerivation convert(SBOLDocument doc, CombinatorialDerivation input) throws SBOLGraphException, SBOLValidationException {
		org.sbolstandard.core2.CombinatorialDerivation cd = doc.createCombinatorialDerivation(Util.getURIPrefix(input),	input.getDisplayId(), Util.getVersion(input), Util.createSBOL2Uri(input.getTemplateURI()));
		Util.copyIdentified(input, cd, doc);
		if (input.getStrategy() != null) {
			cd.setStrategy(convertStrategy31_23(input.getStrategy()));
		}
		if (input.getVariableFeatures() != null) {
			for (VariableFeature vFeature : input.getVariableFeatures()) {
				convertVariableFeature(cd, vFeature);
			}
		}
		return cd;
	}
    
    /**
     * Converts strategy VariableFeature from SBOL3 to VariableComponent from SBOL2 and add it to given CombinatorialDerivation
     * @throws SBOLGraphException
     */
    private VariableComponent convertVariableFeature(org.sbolstandard.core2.CombinatorialDerivation cDerivation, VariableFeature vFeature)  throws SBOLGraphException, SBOLValidationException {
    	Set<org.sbolstandard.core2.Component> sbol2Components = cDerivation.getTemplate().getComponents();
		//URI sbol3FeatureURI=Util.createSBOL3Uri(vComponent.getVariableURI(), parameters);
		org.sbolstandard.core2.Component foundComponent=null;
		for (org.sbolstandard.core2.Component sbol2Comp : sbol2Components){
			if (sbol2Comp.getDisplayId().equals(vFeature.getVariable().getDisplayId())){
				foundComponent=sbol2Comp;
				break;
			}
		}

		VariableComponent vComponent=null;
		if (foundComponent!=null){		
			vComponent = cDerivation.createVariableComponent(vFeature.getDisplayId(), convertCardinality(vFeature.getCardinality()), foundComponent.getIdentity());// Util.createSBOL2Uri(vFeature.getVariable().getUri()));
			if (vFeature.getVariantCollectionURIs() != null) {
				for (URI uri : vFeature.getVariantCollectionURIs()) {
					vComponent.addVariantCollection(Util.createSBOL2Uri(uri));
				}
			}
			if (vFeature.getVariantDerivationURIs() != null) {
				for (URI uri : vFeature.getVariantDerivationURIs()) {
					vComponent.addVariantDerivation(Util.createSBOL2Uri(uri));
				}
			}
			if (vFeature.getVariantURIs() != null) {
				for (URI c : vFeature.getVariantURIs()) {
					vComponent.addVariant(Util.createSBOL2Uri(c));
				}
			}
		}
		return vComponent;
	}
    
    /**
     * Converts strategy CombinatorialDerivationStrategy from SBOL3 to StrategyType from SBOL2
     * @throws SBOLGraphException
     */
    private static StrategyType convertStrategy31_23(CombinatorialDerivationStrategy strategy) throws SBOLGraphException {
		switch (strategy) {
		case Enumerate: {
			return StrategyType.ENUMERATE;
		}
		case Sample: {
			return StrategyType.SAMPLE;
		}
		default: {
			throw new SBOLGraphException("Unknown strategy type " + strategy.toString());
		}
		}
	}

    /**
     * Converts strategy VariableFeatureCardinality from SBOL3 to OperatorType from SBOL2
     * @throws SBOLGraphException
     */
	private static OperatorType convertCardinality(VariableFeatureCardinality cardinality) throws SBOLGraphException {
		switch (cardinality) {
		case ZeroOrOne: {
			return OperatorType.ZEROORONE;
		}
		case One: {
			return OperatorType.ONE;
		}
		case OneOrMore: {
			return OperatorType.ONEORMORE;
		}
		case ZeroOrMore: {
			return OperatorType.ZEROORMORE;
		}
		default: {
			throw new SBOLGraphException("Unknown cardinality " + cardinality.toString());
		}
		}
	}
}