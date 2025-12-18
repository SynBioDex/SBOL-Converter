package org.sbolstandard.converter.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
import org.junit.jupiter.api.Test;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.util.RDFUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.SBOLUtil;

/**
 * Unit test for simple App.
 */
public class TempTest {

        @Test
        public void TestSBOL2Temp() throws Exception {
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/pICH43844.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/RepressionModel.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/RepressionModel.xml"),
                // true,"output/RepressionModel", true);
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("output/simple_attachment_plan_ann_v2.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/CreateAndRemoveModel.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/AnnotationOutput.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/singleModuleDefinition.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/test_source_location.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/ModuleDefinitionOutput.xml"), false,null,true);
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/memberAnnotations_interaction.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/sequence4.xml"));

                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/memberAnnotations.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/partial_pIKE_right_casette.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/partial_pTAK_left_cassette.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl_noRange.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/partial_pIKE_left_cassette.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/partial_pIKE_right_cassette.xml"));
                // ERROR: due to the SBOL2 issues
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput.xml"));

                // TODO: Do another round trip using the final SBOL2 file. It contains a
                // sequence URI but not the sequence itself now!
                // OK: List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/eukaryotic_transcriptional_cd_sa_gl.xml"));

                // ERROR: due to the SBOL2 issues
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/ModuleDefinitionOutput.xml"));

                // ERROR: due to the SBOL2 issues
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl.xml"));

                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl_cd_sa_comp.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/partial_pTAK_right_cassette.xml"));

                // ERROR: due to the SBOL2 issues
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/toggle.xml"));

                /*
                 * https://github.com/SynBioDex/SBOLTestSuite/blob/master/SBOL2/
                 * test_source_location.xml
                 * cd_comp
                 * cd_base_1: 4-14 (source loc): ttgacagctagctcagtcctaggtataatgctagc : The first
                 * 11 bases
                 * cd_base2: 4-14 (source loc): ttgacagctagctcagtcctaggtataatgctagttagcgc: The
                 * first 11 bases
                 * 
                 * seq_comp:
                 * 1-11
                 * 12-22
                 * acagctagctcacagctagctc
                 * 
                 */
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/simple_attachment_plan_ann.xml"));
                // List<String> errors=TestUtil.roundTripConvert(new
                // File("../SBOLTestSuite/SBOL2/Provenance_CodonOptimization.xml"));

                //createSortedNtriples(file, fileOutputNt, fileOutputNtSorted);
                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/sequence1.xml"), true, "output/tmp/sequence1.xml", true);
                //Incomplete:
                Configuration.getInstance().setCompleteDocument(false);
                Configuration.getInstance().setValidateAfterSettingProperties(false);

                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/ModuleDefinitionOutput_maps_md_mod.xml"), true, "output/tmp/ModuleDefinitionOutput_maps_md_mod.xml", true);                
                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/eukaryotic_transcriptional_cd_com_sa_sc_range_ann.xml"), true, "output/tmp/eukaryotic_transcriptional_cd_com_sa_sc_range_ann.xml", true);                
                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/ModuleDefinitionOutput_pa_maps_mod_int_md.xml"), true, "output/tmp/ModuleDefinitionOutput_pa_maps_mod_int_md.xml", true);                
                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/attachment_pointers.xml"), false, "output/tmp/attachment_pointers.xml", true);                
                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/ModuleDefinitionOutput_pa_int_md_mod_fc.xml"), false, "output/tmp/ModuleDefinitionOutput_pa_int_md_mod_fc.xml", true);                
                //List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/gfp_reporter_combDeri.xml"), false, "output/tmp/gfp_reporter_combDeri.xml", true);                
                List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/eukaryotic_transcriptional_unit_enumerated.xml"), false, "output/tmp/eukaryotic_transcriptional_unit_enumerated.xml", true);                
               // List<String> errors = TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2_ic/god_example.xml"), false, "output/tmp/god_example.xml", true);                
           /*     
               Twice:
               backport2_3:sbol2OriginalURI rdf:resource="http://michael.zhang/Eukaryotic_Transcriptional_Unit/Eukaryotic_Transcriptional_Unit_SequenceAnnotation/1
                <backport2_3:sbol2OriginalURI rdf:resource="http://michael.zhang/Eukaryotic_Transcriptional_Unit/Pro_Component/1"/>
                
<sbol:SubComponent rdf:about="http://michael.zhang/1/Eukaryotic_Transcriptional_Unit/Pro_Component">
<sbol:hasLocation>
<sbol:Range rdf:about="http://michael.zhang/1/Eukaryotic_Transcriptional_Unit/Pro_Component/Eukaryotic_Transcriptional_Unit_SequenceAnnotation_Range">
...
</sbol:Range>
</sbol:hasLocation>
<backport2_3:sbol2OriginalURI rdf:resource="http://michael.zhang/Eukaryotic_Transcriptional_Unit/Eukaryotic_Transcriptional_Unit_SequenceAnnotation/1"/>
<backport2_3:sbol2OriginalSequenceAnnotationURI rdf:resource="http://michael.zhang/Eukaryotic_Transcriptional_Unit/Eukaryotic_Transcriptional_Unit_SequenceAnnotation/1"/>
<backport2_3:sbol2OriginalURI rdf:resource="http://michael.zhang/Eukaryotic_Transcriptional_Unit/Pro_Component/1"/>
<sbol:instanceOf rdf:resource="http://michael.zhang/1/Pro"/>
<sbol:displayId>Pro_Component</sbol:displayId>
</sbol:SubComponent>

 */
                TestUtil.DisplayErrors(errors);
                if (errors != null && errors.size() > 0) {
                        // throw new Exception("Conversion produced errors");
                }
        }


}

/*
 * <sbol:location>
 * <sbol:Range rdf:about="http://partsregistry.org/cd/cd_comp/base_s2/base_c2">
 * <sbol:persistentIdentity
 * rdf:resource="http://partsregistry.org/cd/cd_comp/base_s2/base_c2"/>
 * <sbol:displayId>base_c2</sbol:displayId>
 * <sbol:start>12</sbol:start>
 * <sbol:end>22</sbol:end>
 * <sbol:sequence rdf:resource="http://partsregistry.org/seq/seq_comp"/>
 * </sbol:Range>
 * </sbol:location>
 * 
 * 
 * 
 * <sbol:location>
 * <sbol:Range rdf:about="http://partsregistry.org/cd/cd_comp/base_s2/base_c2">
 * <sbol:persistentIdentity
 * rdf:resource="http://partsregistry.org/cd/cd_comp/base_s2/base_c2"/>
 * <sbol:displayId>base_c2</sbol:displayId>
 * <sbol:start>12</sbol:start>
 * <sbol:end>22</sbol:end>
 * </sbol:Range>
 * </sbol:location>
 * 
 * 
 */

/*
 * 
 * <http://partsregistry.org/cd/cd_comp/base1/base_c1>
 * rdf:type sbol:Range;
 * sbol:displayId "base_c1";
 * sbol:end "11";
 * sbol:hasSequence <http://partsregistry.org/seq/seq_comp>;
 * sbol:start "1" .
 * 
 * 
 * 
 * 
 * <http://partsregistry.org/cd/cd_comp/base2>
 * rdf:type sbol:SubComponent;
 * sbol:displayId "base2";
 * sbol:hasLocation <http://partsregistry.org/cd/cd_comp/base2/base_c2>;
 * sbol:instanceOf <http://partsregistry.org/cd/cd_base_2>;
 * sbol:sourceLocation <http://partsregistry.org/cd/cd_comp/base2/seq_base2>;
 * backport2_3:sbol2OriginalSequenceAnnotationURI
 * <http://partsregistry.org/cd/cd_comp/base_s2> .
 * 
 * 
 * 
 * 
 * --------
 * Converted from SBOL2 to SBOL3:
 * PREFIX CHEBI: <https://identifiers.org/CHEBI:>
 * PREFIX EDAM: <https://identifiers.org/edam:>
 * PREFIX GO: <https://identifiers.org/GO:>
 * PREFIX SBO: <https://identifiers.org/SBO:>
 * PREFIX SO: <https://identifiers.org/SO:>
 * PREFIX backport2_3: <https://sbols.org/backport/2_3#>
 * PREFIX dcterms: <http://purl.org/dc/terms/>
 * PREFIX om: <http://www.ontology-of-units-of-measure.org/resource/om-2/>
 * PREFIX pr: <http://partsregistry.org/>
 * PREFIX prov: <http://www.w3.org/ns/prov#>
 * PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
 * PREFIX sbol: <http://sbols.org/v3#>
 * 
 * <http://partsregistry.org/seq/seq_base2>
 * rdf:type sbol:Sequence;
 * sbol:displayId "seq_base2";
 * sbol:elements "ttgacagctagctcagtcctaggtataatgctagttagcgc";
 * sbol:encoding EDAM:format_1207;
 * sbol:hasNamespace pr:seq .
 * 
 * <http://partsregistry.org/cd/cd_base_2>
 * rdf:type sbol:Component;
 * sbol:displayId "cd_base_2";
 * sbol:hasNamespace pr:cd;
 * sbol:hasSequence <http://partsregistry.org/seq/seq_base2>;
 * sbol:role SO:0000167;
 * sbol:type SBO:0000251 .
 * 
 * <http://partsregistry.org/cd/cd_comp/base1/seq_base1>
 * rdf:type sbol:Range;
 * sbol:displayId "seq_base1";
 * sbol:end "14";
 * sbol:hasSequence <http://partsregistry.org/seq/seq_base1>;
 * sbol:orientation SO:0001030;
 * sbol:start "4" .
 * 
 * <http://partsregistry.org/cd/cd_comp/base1>
 * rdf:type sbol:SubComponent;
 * sbol:displayId "base1";
 * sbol:hasLocation <http://partsregistry.org/cd/cd_comp/base1/base_c1>;
 * sbol:instanceOf <http://partsregistry.org/cd/cd_base_1>;
 * sbol:sourceLocation <http://partsregistry.org/cd/cd_comp/base1/seq_base1>;
 * backport2_3:sbol2OriginalSequenceAnnotationURI
 * <http://partsregistry.org/cd/cd_comp/base_s1> .
 * 
 * <http://partsregistry.org/seq/seq_base1>
 * rdf:type sbol:Sequence;
 * sbol:displayId "seq_base1";
 * sbol:elements "ttgacagctagctcagtcctaggtataatgctagc";
 * sbol:encoding EDAM:format_1207;
 * sbol:hasNamespace pr:seq .
 * 
 * <http://partsregistry.org/seq/seq_comp>
 * rdf:type sbol:Sequence;
 * sbol:displayId "seq_comp";
 * sbol:elements "acagctagctcacagctagctc";
 * sbol:encoding EDAM:format_1207;
 * sbol:hasNamespace pr:seq .
 * 
 * <http://partsregistry.org/cd/cd_base_1>
 * rdf:type sbol:Component;
 * sbol:displayId "cd_base_1";
 * sbol:hasNamespace pr:cd;
 * sbol:hasSequence <http://partsregistry.org/seq/seq_base1>;
 * sbol:role SO:0000167;
 * sbol:type SBO:0000251 .
 * 
 * <http://partsregistry.org/cd/cd_comp/Interface1>
 * rdf:type sbol:Interface;
 * sbol:displayId "Interface1";
 * sbol:nondirectional <http://partsregistry.org/cd/cd_comp/base2> ,
 * <http://partsregistry.org/cd/cd_comp/base1> .
 * 
 * <http://partsregistry.org/cd/cd_comp/base2/base_c2>
 * rdf:type sbol:Range;
 * sbol:displayId "base_c2";
 * sbol:end "22";
 * sbol:hasSequence <http://partsregistry.org/seq/seq_comp>;
 * sbol:start "12" .
 * 
 * <http://partsregistry.org/cd/cd_comp/base2>
 * rdf:type sbol:SubComponent;
 * sbol:displayId "base2";
 * sbol:hasLocation <http://partsregistry.org/cd/cd_comp/base2/base_c2>;
 * sbol:instanceOf <http://partsregistry.org/cd/cd_base_2>;
 * sbol:sourceLocation <http://partsregistry.org/cd/cd_comp/base2/seq_base2>;
 * backport2_3:sbol2OriginalSequenceAnnotationURI
 * <http://partsregistry.org/cd/cd_comp/base_s2> .
 * 
 * <http://partsregistry.org/cd/cd_comp/base2/seq_base2>
 * rdf:type sbol:Range;
 * sbol:displayId "seq_base2";
 * sbol:end "14";
 * sbol:hasSequence <http://partsregistry.org/seq/seq_base2>;
 * sbol:orientation SO:0001030;
 * sbol:start "4" .
 * 
 * <http://partsregistry.org/cd/cd_comp/base1/base_c1>
 * rdf:type sbol:Range;
 * sbol:displayId "base_c1";
 * sbol:end "11";
 * sbol:hasSequence <http://partsregistry.org/seq/seq_comp>;
 * sbol:start "1" .
 * 
 * <http://partsregistry.org/cd/cd_comp>
 * rdf:type sbol:Component;
 * sbol:description "Constitutive promoter";
 * sbol:displayId "cd_comp";
 * sbol:hasFeature <http://partsregistry.org/cd/cd_comp/base1> ,
 * <http://partsregistry.org/cd/cd_comp/base2>;
 * sbol:hasInterface <http://partsregistry.org/cd/cd_comp/Interface1>;
 * sbol:hasNamespace pr:cd;
 * sbol:hasSequence <http://partsregistry.org/seq/seq_comp>;
 * sbol:name "cd_comp";
 * sbol:role SO:0000167;
 * sbol:type SBO:0000251 .
 */

/*
 * <sbol:Component rdf:about="http://partsregistry.org/cd/cd_comp/base1">
 * <sbol:persistentIdentity
 * rdf:resource="http://partsregistry.org/cd/cd_comp/base1"/>
 * <sbol:displayId>base1</sbol:displayId>
 * <sbol:access rdf:resource="http://sbols.org/v2#public"/>
 * <sbol:definition rdf:resource="http://partsregistry.org/cd/cd_base_1"/>
 * </sbol:Component>
 * 
 * 
 * 
 * <http://partsregistry.org/cd/cd_comp/base1>
 * rdf:type sbol:SubComponent;
 * sbol:displayId "base1";
 * sbol:hasLocation <http://partsregistry.org/cd/cd_comp/base1/base_c1>;
 * sbol:instanceOf <http://partsregistry.org/cd/cd_base_1>;
 * sbol:sourceLocation <http://partsregistry.org/cd/cd_comp/base1/seq_base1>;
 * backport2_3:sbol2OriginalSequenceAnnotationURI
 * <http://partsregistry.org/cd/cd_comp/base_s1> .
 * 
 */

/*
 * 
 * <sbol:Range rdf:about="http://partsregistry.org/cd/cd_comp/base_s2/base_c2">
 * <sbol:persistentIdentity
 * rdf:resource="http://partsregistry.org/cd/cd_comp/base_s2/base_c2"/>
 * <sbol:displayId>base_c2</sbol:displayId>
 * <sbol:start>12</sbol:start>
 * <sbol:end>22</sbol:end>
 * <sbol:sequence rdf:resource="http://partsregistry.org/seq/seq_comp"/>
 * </sbol:Range>
 * 
 * 
 * <sbol:Range rdf:about="http://partsregistry.org/cd/cd_comp/base_s2/base_c2">
 * <sbol:persistentIdentity
 * rdf:resource="http://partsregistry.org/cd/cd_comp/base_s2/base_c2"/>
 * <sbol:displayId>base_c2</sbol:displayId>
 * <sbol:start>12</sbol:start>
 * <sbol:end>22</sbol:end>
 * </sbol:Range>
 * 
 */