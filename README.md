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

### SBOLTestSuite - Files that could only be converted after the validation was turned off
* Location entities exist with no sequence entity
    * partial_pIKE_right_casette.xml
    * partial_pTAK_left_cassette.xml
    * partial_pIKE_right_casette.xml
    * memberAnnotations.xml
    * ComponentDefinitionOutput_gl_noRange.xml
    * partial_pIKE_left_cassette.xml
    * partial_pIKE_right_cassette.xml
    * ComponentDefinitionOutput.xml
* AnnotationOutput.xml: The content is exactly the same. However, the order of two annotation propoerties are different and libSBOLj2 reports the files as different. This is not an issue!
* SBOL2 Files include incorrect resource URIs. The converter fixes them during the conversion.
    * simple_attachment_plan_ann.xml: Invalid SBOL2 annotation URI
    * singleModel.xml: Invalid SBOL2 model source
* Incorrect AminoAcid sequence, which includes the . character
    * CreateAndRemoveModel.xml
    * multipleSequences.xml


 
## To download the SBOLTestSuite as a dependency
git submodule update --init --recursive
