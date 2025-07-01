package org.sbolstandard.converter.sbol31_23;



import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.RefinementType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.util.SBOLGraphException;


public class ComponentReferenceToMapsToConverter implements ChildEntityConverter<ComponentReference, org.sbolstandard.core2.MapsTo> {

    @Override
    public MapsTo convert(SBOLDocument document, Identified sbol2Parent,
            org.sbolstandard.core3.entity.Identified sbol3Parent, ComponentReference componentReference)
            throws SBOLGraphException, SBOLValidationException {
            

                Component sbol3ParentComponent = (Component) sbol3Parent;

                Feature remoteFeature = componentReference.getRefersTo();
                
                String displayId = componentReference.getDisplayId();
                
                RefinementType refinementType = null;

                String sbol2RemoteFeatureDisplayId = remoteFeature.getDisplayId();

                ConstraintToRefinementTypeConverter constraintToMapsToConverter = new ConstraintToRefinementTypeConverter();

                Constraint currentConstraint = null;
                
				for (org.sbolstandard.core3.entity.Constraint constraint : sbol3ParentComponent.getConstraints()) {
					if (Util.isMapstoConstraint(constraint)) {
						//TODO: implement this
						// CHECK
						if (constraint.getSubject().getUri().equals(componentReference.getUri()) || constraint.getObject().getUri().equals(componentReference.getUri())) {
                            refinementType = constraintToMapsToConverter.convert(document, sbol2Parent, sbol3ParentComponent, constraint);
                            currentConstraint = constraint;
                        }
					}
				}

                if (currentConstraint==null) {
                    throw new SBOLGraphException("No constraint found for MapsTo conversion for ComponentReference: " + componentReference.getDisplayId());
                }
                // Local will be decided using constarint
                String sbol2LocalFuncCompDisplayId = getLocalFunctionalComponentDisplayId(currentConstraint);

                MapsTo mapsTo = null;
                if (sbol2Parent instanceof org.sbolstandard.core2.Module) {
                    org.sbolstandard.core2.Module sbol2Module = (org.sbolstandard.core2.Module) sbol2Parent;
                    System.out.println("Creating MapsTo for a Module: " + sbol2Module.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
                    mapsTo = sbol2Module.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
                
                } else if (sbol2Parent instanceof org.sbolstandard.core2.Component) {
                    org.sbolstandard.core2.Component sbol2Component = (org.sbolstandard.core2.Component) sbol2Parent;
                    System.out.println("Creating MapsTo for a Component: " + sbol2Component.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
                    mapsTo = sbol2Component.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
                }
               
                return mapsTo;
    }
    
    
    private String getLocalFunctionalComponentDisplayId(Constraint currentConstraint) throws SBOLGraphException {

            Feature localFeatureForFuncComp= null;

            if (currentConstraint.getSubject() instanceof org.sbolstandard.core3.entity.ComponentReference) {
                localFeatureForFuncComp = currentConstraint.getObject();
            } else if (currentConstraint.getObject() instanceof org.sbolstandard.core3.entity.ComponentReference) {
                localFeatureForFuncComp = currentConstraint.getSubject();
            } else {
                throw new SBOLGraphException("Unknown constraint type for MapsTo conversion: " + currentConstraint);
            }
            return localFeatureForFuncComp.getDisplayId();
        
    }

    // private MapsTo convertOLD(SBOLDocument document, Identified sbol2parent,
    //         org.sbolstandard.core3.entity.Identified sbol3Parent, ComponentReference componentReference)
    //         throws SBOLGraphException, SBOLValidationException {
                
    //             org.sbolstandard.core2.Module sbol2ParentModule = (org.sbolstandard.core2.Module) sbol2parent;
    //             ModuleDefinition sbol2Definition = sbol2ParentModule.getDefinition();

    //             Component sbol3ParentComponent = (Component) sbol3Parent;

    //             SubComponent subComponentForModule = componentReference.getInChildOf();
    //             // TODO: CHECK IF IT IS TRUE (Remote SubComponent)
    //             Feature remoteFeature = componentReference.getRefersTo();
    //             // This should be the remote of the MapsTo

    //             // We need below information to create the MapsTo
    //             String displayId = componentReference.getDisplayId();
                
    //             RefinementType refinementType = null;

    //             String sbol2RemoteFeatureDisplayId = remoteFeature.getDisplayId();

    //             ConstraintToRefinementTypeConverter constraintToMapsToConverter = new ConstraintToRefinementTypeConverter();

    //             Constraint currentConstraint = null;
    //             // This shoul be inside of the compRefToMapsToConverter since a refinement is necessary to create the MapsTo
	// 			for (org.sbolstandard.core3.entity.Constraint constraint : sbol3ParentComponent.getConstraints()) {
	// 				if (Util.isMapstoConstraint(constraint)) {
	// 					//TODO: implement this
	// 					// CHECK
	// 					if (constraint.getSubject().getUri().equals(componentReference.getUri()) || constraint.getObject().getUri().equals(componentReference.getUri())) {
    //                         refinementType = constraintToMapsToConverter.convert(document, sbol2ParentModule, sbol3ParentComponent, constraint);
    //                         currentConstraint = constraint;
    //                     }
	// 				}
	// 			}

    //             if (currentConstraint==null) {
    //                 throw new SBOLGraphException("No constraint found for MapsTo conversion for ComponentReference: " + componentReference.getDisplayId());
    //             }
    //             // Local will be decided using constarint
    //             String sbol2LocalFuncCompDisplayId = getLocalFunctionalComponentDisplayId(currentConstraint);

    //             System.out.println("Creating MapsTo for: " + sbol2ParentModule.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
    //             MapsTo mapsTo = sbol2ParentModule.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
                
    //             return mapsTo;
    // }

}
