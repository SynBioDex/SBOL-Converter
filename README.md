# SBOL-Converter

The Java library has been developed to convert [Synthetic Biology Open Language version 3 (SBOL3) and SBOL2](https://sbolstandard.org/data-model-specification) files. The convesion can be in either direction, from SBOL2 to SBOL3 and vice versa.

It can also be used to convert:

- GenBank,
- FASTA,
- SnapGene,
- GFF3 and
- CSV files.

The library is under development and is currently available as an beta release.

## Download

Please download the latest relase from the link below:

https://github.com/SynBioDex/SBOL-Converter/releases

### Installation from the Source

First, download the project and install it using Maven.

```
git clone https://github.com/SynBioDex/SBOL-Converter.git
cd sbol-converter
mvn install -DskipTests=true
```

The jar file can be found under the target file:

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar
```

Then include it as a Maven dependency in your project's POM file.

```
</dependencies>
	...
   <dependency>
      <groupId>org.sbolstandard</groupId>
      <artifactId>sbol-converter</artifactId>
      <version>1.0.2-SNAPSHOT</version>
   </dependency>
   ...
</dependencies>

```

## How to use the converter:

The library provides separete converters from SBOL2-to-SBOL3 and SBOL3-to-SBOL2. Each converter takes an an SBOL document to be converted and returns a document for the target version.

### From command line

The converter can be used with the following command

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar <inputFile> [options]  [-o <outputFile>]
```

Convertion Options:

- -l <language> specfies language (SBOL1/SBOL2/GenBank/FASTA/SnapGene/GFF3/CSV) for output (default=SBOL2)

Validation Options for SBOL3:

- -i allow SBOL document to be incomplete
- -b check best practices
- -no indicate no output file to be generated from validation

Validation Options for SBOL2:

- -s <topLevelURI> select only this object and those it references
- -p <URIprefix> used for converted objects
- -c change URI prefix to specified <URIprefix>
- -v <version> used for converted objects
- -t uses types in URIs
- -n allow non-compliant URIs (Not applicable for SBOL3 validation)
- -i allow SBOL document to be incomplete
- -b check best practices
- -f fail on first error
- -d display detailed error trace
- -mf main SBOL file if file diff. option is selected
- -cf second SBOL file if file diff. option is selected
- -no indicate no output file to be generated from validation
- -en enumerate CombinatorialDerivations

#### Converting to/from SBOL3

Examples:

1. Converting from SBOL2 to SBOL3 and displaying the result in CLI:

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
 ../test_files/sbol2TestFile.xml \
-l SBOL3
```

2. Converting from SBOL2 to SBOL3 and writing the result in a file:

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol2TestFile.xml \
-l SBOL3 \
-o ../test_files/outputs/convFromSBOL2toSBOL3File.ttl
```

3. Converting from SBOL3 to SBOL2 and writing the result in a file:

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol3TestFile.ttl \
-l SBOL2 \
-o ../test_files/outputs/convFromSBOL3toSBOL2File.xml
```

4. Converting from SBOL3 to GenBank (providing a prefix URI is required)

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol3ShortTest.ttl \
-l GenBank \
-p https://keele.ac.uk \
-o ../test_files/outputs/convFromSBOL3toGenBankFile.gb
```

5. Converting from GenBank to SBOL3 (providing a prefix URI is required)

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
../test_files/genBankTestFile.gb \
 -l SBOL3 -p https://keele.ac.uk  \
 -o ../test_files/outputs/convFromGenBanktoSBOL3File.ttl;
```

#### Validation

1. Validating an SBOL3 file:

```
java -jar target/sbol-converter-1.0.3-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol3TestFile.ttl
```

2. Validating an SBOL3 file checking best practices:

```
java -jar target/sbol-converter-1.0.3-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol3TestFile.ttl \
-b
```

3. Validating an SBOL3 file allowing incomplete documents:

```
java -jar target/sbol-converter-1.0.3-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol3TestFile.ttl \
-i
```

4. Validating an SBOL3 file without output:

```
java -jar target/sbol-converter-1.0.3-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol3TestFile.ttl \
-no
```

5. To see the error of an invalid SBOL3 file that do not follow best practices:

```
java -jar target/sbol-converter-1.0.3-SNAPSHOT-jar-with-dependencies.jar \
../test_files/invalid.ttl \
-b
```

6. Validating an SBOL2 file:

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
../test_files/sbol2TestFile.xml
```

#### Converting to/from GenBank, FASTA, etc.

1. Converting from GenBank/FASTA/... to SBOL2 (providing a prefix URI is required)

```
java -jar target/sbol-converter-1.0.2-SNAPSHOT-jar-with-dependencies.jar \
  ../test_files/genBankTestFile.gb \
  -l SBOL2 \
  -p https://keele.ac.uk/scm \
  -o ../test_files/outputs/convFromGenBanktoSBOL2File.xml

```

### Using the code

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

sbol2:SequenceAnnotation.displayId = {sbol3:SubComponent.displayId}\_{sbol3:Location.displayId}

### Incorrect URIs

It is possible to include String rather than URI for resources in SBOL2. The coverter appends the following to convert them into valid URIs: https://sbolstandard.org/SBOL3-Converter/
Examples:

- SBOL2/memberAnnotations.xml

```
<sbol:source rdf:resource="someModel_source"/>
```

Changed by the SBOL3 converter as

```
 <sbol:source rdf:resource="https://sbolstandard.org/SBOL3-Converter/someModel_source"/>

```

### SBOLTestSuite/SBOL2 conversion

Number of files successfully converted and roundtripped from SBOL2 to SBOL3 and back: 173/189. Twelve of the files with issues were converted successfully. However, due to the incorrect URIs in source files and inconsistent versioning of child entities causes very small data that could not contribute to 100% conversion.

<strong>Progress: 92%</strong>
<progress value="92" max="100"></progress>


<br>
Number of files successfully converted and validated: 184/189

<strong>Progress: 95%</strong>
<progress value="95" max="100"></progress>

<br>
Number of files successfully converted without validation : 189/189 (All files)

<strong>Progress: 100%</strong><progress value="100" max="100"></progress>

- Could not be converted:

  - Due to incorrect range values. these examples also included locations with no sequences.
    - ComponentDefinitionOutput.xml
    - ModuleDefinitionOutput.xml
    - ComponentDefinitionOutput_gl.xml
    - toggle.xml


- Files with minor differences due to the conversion process:

  - Incorrect URIs for annotation entities in some of the SBOL2 source files. Thse incorrect URIs are fixed during the conversion.
    - AnnotationOutput.xml: The content is exactly the same. However, the resulting child entity's URI has two fragments after the parent. SBOL3 requires one fragment after the parent. Hence, the resulting URIs are different.
      - SBOL2 parent entity URI: .../BBa_J23119
      - SBOL2 child entity URI: ...BBa_J23119/anno/information
      - The new SBOL3 child entity URI: ...BBa_J23119/information
    - simple_attachment_plan_ann.xml: Invalid SBOL2 annotation URI
    - memberAnnotations.xml: Invalid SBOL2 model source
    - singleModel.xml: Invalid SBOL2 model source
    - attachment_ann.xml: Invalid SBOL2 source URI
    - simple_attachment_ref.xml: Invalid SBOL2 source URI
    - attachment.xml: Invalid SBOL2 source URI


<<<<<<< HEAD
### SBOLTestSuite/SBOL3 conversion
Currently, SBOL3-to-SBOL2 conversion is one way and does not store SBOL3 specific content within the converted SBOL2 files, excluding the folders for invalid and urn examples. 
=======
  - Not using version numbers in child entities although parent entities have versions
    - sequence4.xml: The content is the same. However, In SBOL2, the child entities' URIs for annotations do not use version numbers although all child entities use. As a result, SBOL3 annotation entities are created with version numbers causing URI differences only.
    - EF587312.xml: Same reason as above (see sequence4.xml)
    - sequence1.xml: Same reason as above (see sequence4.xml)
    - sequence2.xml: Same reason as above (see sequence4.xml)
    - sequence3.xml: Same reason as above (see sequence4.xml)
    

- Converted with adjustments: 
  - Location entities exist with no sequence entity - converted by creating empty sequences.

    - partial_pIKE_right_casette.xml
    - partial_pTAK_left_cassette.xml
    - partial_pIKE_right_casette.xml
    - memberAnnotations.xml
    - ComponentDefinitionOutput_gl_noRange.xml
    - partial_pIKE_left_cassette.xml
    - partial_pIKE_right_cassette.xml
    - eukaryotic_transcriptional_cd_sa_gl.xml
    - ComponentDefinitionOutput_gl.xml
    - ComponentDefinitionOutput_gl_cd_sa_comp.xml
    - partial_pTAK_right_cassette.xml

  - Allowed the . character in sequences after discussions with the community.
    - CreateAndRemoveModel.xml
    - multipleSequences.xml
    - singleCompDef_withSeq.xml
    - singleSequence.xml
    - memberAnnotations.xml

## To download the SBOLTestSuite as a dependency
>>>>>>> feature/sbol2all

Number of files successfully converted to SBOL2 : 26/30

Failed cases:
* combine2020.rdf: Includes interactions between ComponentReferences and could not be fully mapped to SBOL2 entities.
* componentreference.rdf: The example is to demonstrate the use of a single component reference entity only and does not containt the constraints. Hence, it can't be converted, although the file is a valid SBOL3 file.
* participation.rdf. This example also uses ComponentReferences as interaction participants and could not be converte.
* constraint.rdf: The example is to demonstrate the use of a constraint entity. One of the component reference entity does not have a corresponding constraint. Hence, this example could not be converted to SBOL2, although the file is a valid SBOL3 file.


<strong>Progress: 87%</strong><progress value="87" max="100"></progress>


## Conversion Annotations (Backport)
Backport annotations use two different namespaces from SBOL2 to SBOL3 and vice versa
* https://sbols.org/backport/2_3#: SBOL3 documents converted from SBOL2 will include terms from this namespace
* https://sbols.org/backport/3_2#: SBOL2 documents converted from SBOL2 will include terms from this namespace

### SBOL2-SBOL3 Conversion
* https://sbols.org/backport/2_3#sbol2OriginalSequenceAnnotationURI: Handles sbol2.SequenceAnnotations. The sbol2.SequenceAnnotation and sbol2.Component entities are merged into sbol3.SubComponent entities. During the conversion, an sbol2.SequenceAnnotation entity's URI is stored within the corresponding sbol3.Component entity.
* https://sbols.org/backport/2_3#sbol3TempSequenceURI: Used to track the empty sequences created in SBOL3 documents during the conversion. sbol2:Location entities can have empty sequences while sbol3.Location entities must have a Sequence entity. Hence, during the SBOL2-to-3 conversion, an empty sequence is created.This annotation is used to remove the sequences marked as empty during the SBOL3-to-2 conversion.

## To download the SBOLTestSuite as a dependency

git submodule update --init --recursive
