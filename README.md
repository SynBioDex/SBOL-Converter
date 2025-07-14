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

### SBOLTestSuite.

Number of files converted: 164

Number of files converted after the validation turned off: 164

Files that could only be converted after the validation was turned off:28

* Location entities exist with no sequence entity
    * partial_pIKE_right_casette.xml
    * partial_pTAK_left_cassette.xml
    * partial_pIKE_right_casette.xml
    * memberAnnotations.xml
    * ComponentDefinitionOutput_gl_noRange.xml
    * partial_pIKE_left_cassette.xml
    * partial_pIKE_right_cassette.xml
    * ComponentDefinitionOutput.xml
    * eukaryotic_transcriptional_cd_sa_gl.xml
    * ModuleDefinitionOutput.xml
    * ComponentDefinitionOutput_gl.xml
    * ComponentDefinitionOutput_gl_cd_sa_comp.xml
    * partial_pTAK_right_cassette.xml
    * toggle.xml
* Different URIs for annotation entities    
  * AnnotationOutput.xml: The content is exactly the same. However, the resulting child entity's URI has two fragments after the parent. SBOL3 requires one fragment after the parent. Hence, the resulting URIs are different. 
    * SBOL2 parent entity URI: .../BBa_J23119
    * SBOL2 child entity URI: ...BBa_J23119/anno/information
    * The new SBOL3 child entity URI: ...BBa_J23119/information
  
* Not using version numbers in child entities although parent entities have versions
  * sequence4.xml: The content is the same. However, In SBOL2, the child entities' URIs for annotations do not use version numbers although all child entities use. As a result, SBOL3 annotation entities are created with version numbers cauing URI differences only.
  * EF587312.xml: Same reason as above (see sequence4.xml)
  * sequence1.xml: Same reason as above (see sequence4.xml)
  * sequence2.xml: Same reason as above (see sequence4.xml)

* SBOL2 Files include incorrect resource URIs. The converter fixes them during the conversion.
    * simple_attachment_plan_ann.xml: Invalid SBOL2 annotation URI
    * singleModel.xml: Invalid SBOL2 model source
    * attachment_ann.xml: Invalid SBOL2 source URI
    * simple_attachment_ref.xml: Invalid SBOL2 source URI
    * attachment.xml: Invalid SBOL2 source URI
    
* Incorrect AminoAcid sequence, which includes the . character
    * CreateAndRemoveModel.xml
    * multipleSequences.xml
    * singleCompDef_withSeq.xml
    * singleSequence.xml


 
## To download the SBOLTestSuite as a dependency
git submodule update --init --recursive
