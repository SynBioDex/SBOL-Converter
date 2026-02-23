package org.sbolstandard.converter.sbol31_23;
import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.RefinementType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.util.SBOLGraphException;


public class ComponentReferenceToMapsToConverter //implements ChildEntityConverter<ComponentReference, org.sbolstandard.core2.MapsTo> 
{

   // @Override
    public MapsTo convert(SBOLDocument document, Identified sbol2Parent,
            org.sbolstandard.core3.entity.Identified sbol3Parent, ComponentReference componentReference, Identified sbol2TopLevelParent)
            throws SBOLGraphException, SBOLValidationException {
            
        Component sbol3ParentComponent = (Component) sbol3Parent;
        Feature remoteFeature = componentReference.getRefersTo();                
        String displayId = componentReference.getDisplayId();                
        RefinementType refinementType = null;
        String sbol2RemoteFeatureDisplayId = remoteFeature.getDisplayId();

        ConstraintToRefinementTypeConverter constraintToMapsToConverter = new ConstraintToRefinementTypeConverter();
        Constraint currentConstraint = null;    
        if (sbol3ParentComponent.getConstraints()!=null){
            for (org.sbolstandard.core3.entity.Constraint constraint : sbol3ParentComponent.getConstraints()) {
                if (Util.isMapstoConstraint(constraint)) {
                    if (constraint.getSubject().getUri().equals(componentReference.getUri()) || constraint.getObject().getUri().equals(componentReference.getUri())) {
                        refinementType = constraintToMapsToConverter.convert(document, sbol2Parent, sbol3ParentComponent, constraint);
                        currentConstraint = constraint;
                        break;
                    }
                }
            }
        }
        
        if (currentConstraint==null) {
            throw new SBOLGraphException("No constraint found for MapsTo conversion for ComponentReference: " + componentReference.getDisplayId());
        }

        // Local will be decided using the constraint
        boolean isCompRefMappedToCompRef=isCompRefMappedToCompRef(currentConstraint);
        MapsTo mapsTo=null;
        if (!isCompRefMappedToCompRef){
            String sbol2LocalFuncCompDisplayId = getLocalFunctionalComponentDisplayId(currentConstraint);        
            mapsTo= createMapsTo(sbol2Parent, sbol3Parent, componentReference, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId, document);
        }
        else {//Create subject_object componentinstance (Component or FunctionalComponent depending on the parent) and then create two mapsTos between the subject_object and the subject and object of the constraint, respectively. This is to be able to represent compRef to compRef mapping in SBOL2.
            ComponentReference subjectCompRef = (ComponentReference)currentConstraint.getSubject();
            ComponentReference objectCompRef = (ComponentReference)currentConstraint.getObject();
            String compInstanceId=subjectCompRef.getDisplayId() + "_mapsTo_" + objectCompRef.getDisplayId();
            URI defUri=URI.create("https://github.com/SynBioDex/SBOLConverter#" + compInstanceId +"_definition");                    
            if (sbol2TopLevelParent instanceof org.sbolstandard.core2.Component){                
                ComponentDefinition sbol2CompDef = (ComponentDefinition) sbol2TopLevelParent;
                if (sbol2CompDef.getComponent(compInstanceId)==null){
                    org.sbolstandard.core2.Component comp=sbol2CompDef.createComponent(compInstanceId, AccessType.PUBLIC, defUri);                            
                }
            }
            else if (sbol2TopLevelParent instanceof ModuleDefinition){
                ModuleDefinition sbol2ModuleDef = (ModuleDefinition) sbol2TopLevelParent;
                if (sbol2ModuleDef.getFunctionalComponent(compInstanceId)==null){
                    sbol2ModuleDef.createFunctionalComponent(compInstanceId, AccessType.PUBLIC, defUri, DirectionType.INOUT);
                }                
            }
          
            mapsTo= createMapsTo(sbol2Parent, sbol3Parent, subjectCompRef, refinementType, compInstanceId, sbol2RemoteFeatureDisplayId, document);
            MapsTo mapsTo2= createMapsTo(sbol2Parent, sbol3Parent, objectCompRef, refinementType, compInstanceId, sbol2RemoteFeatureDisplayId, document);
        }

        /*MapsTo mapsTo = null;
        if (sbol2Parent instanceof org.sbolstandard.core2.Module) {
            org.sbolstandard.core2.Module sbol2Module = (org.sbolstandard.core2.Module) sbol2Parent;
            System.out.println("Creating MapsTo for a Module: " + sbol2Module.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
            mapsTo = sbol2Module.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);                
        } 
        else if (sbol2Parent instanceof org.sbolstandard.core2.Component) {
            org.sbolstandard.core2.Component sbol2Component = (org.sbolstandard.core2.Component) sbol2Parent;
            System.out.println("Creating MapsTo for a Component: " + sbol2Component.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
            mapsTo = sbol2Component.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
        }
        else if (sbol2Parent instanceof org.sbolstandard.core2.FunctionalComponent) {
            org.sbolstandard.core2.FunctionalComponent sbol2Component = (org.sbolstandard.core2.FunctionalComponent) sbol2Parent;
            System.out.println("Creating MapsTo for a Functional Component: " + sbol2Component.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
            mapsTo = sbol2Component.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
        }            
        Util.copyIdentified(componentReference, mapsTo,document);*/
        return mapsTo;
    }


    private MapsTo createMapsTo(Identified sbol2Parent, org.sbolstandard.core3.entity.Identified sbol3Parent, ComponentReference componentReference, RefinementType refinementType, String sbol2LocalFuncCompDisplayId, String sbol2RemoteFeatureDisplayId, SBOLDocument document) throws SBOLGraphException, SBOLValidationException {
        MapsTo mapsTo = null;
        String displayId = componentReference.getDisplayId();
        if (sbol2Parent instanceof org.sbolstandard.core2.Module) {
            org.sbolstandard.core2.Module sbol2Module = (org.sbolstandard.core2.Module) sbol2Parent;
            if (sbol2Module.getMapsTo(displayId)==null){
                System.out.println("Creating MapsTo for a Module: " + sbol2Module.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
                mapsTo = sbol2Module.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);                
            }
        } 
        else if (sbol2Parent instanceof org.sbolstandard.core2.Component) {
            org.sbolstandard.core2.Component sbol2Component = (org.sbolstandard.core2.Component) sbol2Parent;
            if (sbol2Component.getMapsTo(displayId)==null){
                System.out.println("Creating MapsTo for a Component: " + sbol2Component.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
                mapsTo = sbol2Component.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
            }
        }
        else if (sbol2Parent instanceof org.sbolstandard.core2.FunctionalComponent) {
            org.sbolstandard.core2.FunctionalComponent sbol2Component = (org.sbolstandard.core2.FunctionalComponent) sbol2Parent;
            if (sbol2Component.getMapsTo(displayId)==null){
                System.out.println("Creating MapsTo for a Functional Component: " + sbol2Component.getDisplayId() + " with refinement type: " + refinementType + " and local functional component: " + sbol2LocalFuncCompDisplayId + " and remote feature: " + sbol2RemoteFeatureDisplayId);
                mapsTo = sbol2Component.createMapsTo(displayId, refinementType, sbol2LocalFuncCompDisplayId, sbol2RemoteFeatureDisplayId);
            }
        }            
        Util.copyIdentified(componentReference, mapsTo,document);
        return mapsTo;
    }
    
    
    private String getLocalFunctionalComponentDisplayId(Constraint currentConstraint) throws SBOLGraphException {
        Feature localFeatureForFuncComp = null;
        if (currentConstraint.getSubject() instanceof org.sbolstandard.core3.entity.ComponentReference) {
            localFeatureForFuncComp = currentConstraint.getObject();
        } else if (currentConstraint.getObject() instanceof org.sbolstandard.core3.entity.ComponentReference) {
            localFeatureForFuncComp = currentConstraint.getSubject();
        } else {
            throw new SBOLGraphException("Unknown constraint type for MapsTo conversion: " + currentConstraint);
        }
        return localFeatureForFuncComp.getDisplayId();
    }


	public static boolean isCompRefMappedToCompRef(Constraint constraint) throws SBOLGraphException {
		if (constraint.getSubject() instanceof org.sbolstandard.core3.entity.ComponentReference
            && constraint.getObject() instanceof org.sbolstandard.core3.entity.ComponentReference) {
                return true;
            }
        return false;
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
