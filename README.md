# SBOL-Converter
The Java library has been developed to convert [Synthetic Biology Open Language version 3 (SBOL3) and SBOL2](https://sbolstandard.org/data-model-specification) files. The convesion can be in either direction, from SBOL2 to SBOL3 and vice versa. The library is  under development and is currently available as an alpha release. 

## Installation
First, download the project and install it using Maven.
```
git clone https://github.com/SynBioDex/SBOL-Converter.git
cd sbol-converter
mvn install -DskipTests=true
```

Then include it as a Maven dependency in your project's POM file.
``` 
</dependencies>
	...
   <dependency>
      <groupId>org.sbolstandard</groupId>
      <artifactId>sbol-converter</artifactId>
      <version>1.0.1-SNAPSHOT</version>
   </dependency>
   ...
</dependencies>

```

## How to use the converter:

The library provides separete converters from SBOL2-to-SBOL3 and SBOL3-to-SBOL2. Each converter takes an an SBOL document to be converted and returns a document for the target version.

Converting from SBOL2 to SBOL3:
```
SBOLDocumentConverter converter = new SBOLDocumentConverter();
org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(sbol2InputDocument);
```	

Converting from SBOL3 to SBOL2:
```
org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);
```	

## Converter decisions

### Converting sbol2:SequenceAnnotation and sbol2:Component entities to sbol3:Feature (sbol2:SequenceFeature or sbol2:SubComponent) entities:

 sbol2:SequenceAnnotation.roles and sbol2:SubComponent.roles are converted as sbol3:Feature.roles. However, when converting from sbol3 to sbol2, all sbol3:Feature.roles are converted as sbol2:Component roles.

### Converting sbol3:SubComponent to sbol2:Component and sbol2:SequenceAnnotations:

 sbol3:SubComponent.displayId is used to create sbol2:Component.displayId. 
 
 An sbol2:SequenceAnnotation is created for each location in a sbol3:SubComponent. Hence: 
 
 sbol2:SequenceAnnotation.displayId = {sbol3:SubComponent.displayId}_{sbol3:Location.displayId}

 ### Incorrect URIs
 It is possible to include String rather than URI for resources in SBOL2. The coverter appends the following to convert them into valid URIs: https://sbolstandard.org/SBOL3-Converter/ 
 Examples:

 *  SBOL2/memberAnnotations.xml
 ```
 <sbol:source rdf:resource="someModel_source"/>
```

Changed by the SBOL3 converter as 
```
 <sbol:source rdf:resource="https://sbolstandard.org/SBOL3-Converter/someModel_source"/>

```

### SBOLTestSuite/SBOL2 conversion

Number of files successfully converted and validated: 180/189

<strong>Progress: 95%</strong>
<progress value="95" max="100"></progress>

Number of files successfully converted without validation : 189/189 (All files)

<strong>Progress: 100%</strong><progress value="100" max="100"></progress>


* Could not be converted 
  *  Due to Incorrect range values. these examples also included locations with no sequences.
      * ComponentDefinitionOutput.xml
      * ModuleDefinitionOutput.xml
      * ComponentDefinitionOutput_gl.xml
      * toggle.xml
  * Incorrect AminoAcid sequence, which includes the . character
      * CreateAndRemoveModel.xml
      * multipleSequences.xml
      * singleCompDef_withSeq.xml
      * singleSequence.xml
      * memberAnnotations.xml

* Converted with adjustments: Location entities exist with no sequence entity - converted by creating empty sequences
    * partial_pIKE_right_casette.xml
    * partial_pTAK_left_cassette.xml
    * partial_pIKE_right_casette.xml
    * memberAnnotations.xml
    * ComponentDefinitionOutput_gl_noRange.xml
    * partial_pIKE_left_cassette.xml
    * partial_pIKE_right_cassette.xml
    * eukaryotic_transcriptional_cd_sa_gl.xml
    * ComponentDefinitionOutput_gl.xml
    * ComponentDefinitionOutput_gl_cd_sa_comp.xml
    * partial_pTAK_right_cassette.xml

* Files with minor differences due to the conversion process:
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

  * Incorrect URIs are fixed. SBOL2 Files include incorrect resource URIs. The converter fixes them during the conversion.
    * simple_attachment_plan_ann.xml: Invalid SBOL2 annotation URI
    * singleModel.xml: Invalid SBOL2 model source
    * attachment_ann.xml: Invalid SBOL2 source URI
    * simple_attachment_ref.xml: Invalid SBOL2 source URI
    * attachment.xml: Invalid SBOL2 source URI
    

## To download the SBOLTestSuite as a dependency
git submodule update --init --recursive
