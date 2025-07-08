# SBOL-Converter
SBOL Java Converter

## Converter decisions

### Converting sbol2:SequenceAnnotation and sbol2:Component entities to sbol3:Feature (sbol2:SequenceFeature or sbol2:SubComponent) entities:

 sbol2:SequenceAnnotation.roles and sbol2:SubComponent.roles are converted as sbol3:Feature.roles. However, when converting from sbol3 to sbol2, all sbol3:Feature.roles are converted as sbol2:Component roles.

### Converting sbol3:SubComponent to sbol2:Component and sbol2:SequenceAnnotations:

 sbol3:SubComponent.displayId is used to create sbol2:Component.displayId. 
 
 An sbol2:SequenceAnnotation is created for each location in a sbol3:SubComponent. Hence: 
 
 sbol2:SequenceAnnotation.displayId = {sbol3:SubComponent.displayId_sbol3:Location.displayId}

 ### Incorrect URIs
 It is possible to include String rather than URI for resources in SBOL2. The coverter appends the following to convert them into valid URIs: https://sbolstandard.org/SBOL3-Converter/ 
 Examples:

 *  SBOL2/memberAnnotations.xml
 ```
 <sbol:source rdf:resource="someModel_source"/>
```
changed by the SBOL3 converter as 
```
``` 
 
## To download the SBOLTestSuite as a dependency
git submodule update --init --recursive
