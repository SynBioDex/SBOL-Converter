package org.sbolstandard.converter.tests.datacuration;

import static org.junit.Assert.assertTrue;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.riot.RDFFormat;
import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.App;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Interaction;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Range;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
//import org.sbolstandard.core3.test.TestUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.validation.SBOLComparator;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.InteractionType;
import org.sbolstandard.core3.vocabulary.ParticipationRole;
import org.sbolstandard.core3.vocabulary.Role;
import org.sbolstandard.core3.vocabulary.RestrictionType.ConstraintRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.SequentialRestriction;

//import junit.framework.TestCase;

public class GeneticMemory{

    

    private int notFoundCounter = 0;
    private int foundCounter = 0;


    @Test
    public void testPaperExample() throws SBOLGraphException, IOException, SBOLValidationException, SBOLConversionException
    {
            //http://parts.igem.org/Part:BBa_F2620

            // 1. write names from genbank (label)
            // Read SBOL3 document from local
            // For each component get labels and map names
            // find positions
            // find the dna sequence of this position and assign to component

            String inputFileName = "paperGM.gb";
            String outputFileName = "paper_sbol3_GM.xml";
            String URI_prefix = "https://keele.ac.uk/";
            
            // Send the paper.gbp file to the App main method as an argument to convert to SBOL3
            String[] args = {
                "input/datacuration/"+inputFileName,
                "-l", "SBOL3",
                "-p", URI_prefix,
                "-o", "output/datacuration/"+outputFileName
            };
            //"-o", "src/test/java/org/sbolstandard/converter/tests/datacuration/paper_sbol3.xml"
            App.main(args);

            // Get the converted SBOL3 document
            SBOLDocument convertedDocument = SBOLIO.read(new File("output/datacuration/"+outputFileName), SBOLFormat.RDFXML);
            //Component mainCompConvDoc = convertedDocument.getIdentified(URI.create(URI_prefix + "sequence_97289_e"), Component.class);
            //System.out.println("Components Size: " + convertedDocument.getComponents().size());
            //System.out.println("Component description: " + mainCompConvDoc.getDescription());
            //List<SequenceFeature> sequenceFeatures = mainCompConvDoc.getSequenceFeatures();

            //SBOLDocument newDocument = new SBOLDocument(URI.create("https://synbiohub.org/public/igem/"));
            SBOLDocument newDocument = new SBOLDocument(URI.create(URI_prefix));
            //SBOLDocument doc = new SBOLDocument();
            
            Component device = newDocument.createComponent("sequence_97289_e_pAND_I", Arrays.asList(ComponentType.DNA.getUri()));

            device.setName("sequence_97289_e_pAND_I");

            // MU added on 2026-02-06 for the validation error sbol3-11302 - For every Location that is not an EntireSequence and t
            //device.setSequences(convertedDocument.getSequences());
            Sequence seq = newDocument.createSequence("sequence_97289_e_pAND_I_sequence");
            seq.setElements(convertedDocument.getSequences().get(0).getElements());
            seq.setEncoding(org.sbolstandard.core3.vocabulary.Encoding.NucleicAcid);
            device.addSequence(seq);

            System.out.println("Device Sequences: " + (device.getSequences()==null? 0 : device.getSequences().size()));
            //Component device = SBOLAPI.createDnaComponent(newDocument, "BBa_F2620", "BBa_F2620", "PoPS Receiver", Role.EngineeredGene, ""); 
            
            

            //Component prm_0 = SBOLAPI.createDnaComponent(newDocument, "p0", "p0_promoter", "promoter p0", Role.Promoter, "");
            Component pLacI = createComponentWithSequence("pLacI", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Promoter));
        
            //Component rbs_0 = SBOLAPI.createDnaComponent(newDocument, "rbs0", "rbs0", "rbs0", Role.RBS, "");
            Component lacI_RBS = createComponentWithSequence("lacI_RBS", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.RBS));

            //Component cds_LacI = SBOLAPI.createDnaComponent(newDocument, "LacI", "LacI_CDS", "LacI coding sequence", Role.CDS, "");
            Component cds_LacI = createComponentWithSequence("lacI", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.CDS));
            
            //Component rbs_1 = SBOLAPI.createDnaComponent(newDocument, "rbs1", "rbs1", "rbs1", Role.RBS, "");
            Component tetR_RBS = createComponentWithSequence("tetR_RBS", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.RBS));

            Component cds_TetR = createComponentWithSequence("TetR", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.CDS));
            
            Component ter_0 = createComponentWithSequence("ter0", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Terminator));
            


            Component prm_PTet = createComponentWithSequence("pTeT", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Promoter));
            //Component TetR=SBOLAPI.createProteinComponent(doc, "TetR", "TetR", "TetR Protein", Role.CDS, "");
            
            // UNKNOWN COMPONENT
            Component ins_SccJ = createComponentWithSequence("SccJ_insulator", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(URI.create("http://identifiers.org/so/SO:0000627")));
            
            
            Component srpr_RBS = createComponentWithSequence("srpr_RBS", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.RBS));
            Component cds_SrpR = createComponentWithSequence("srpr", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.CDS));
            Component rrnB_T1_terminator = createComponentWithSequence("rrnB T1 terminator", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Terminator));
            Component prm_PTac = createComponentWithSequence("pTacPromoter", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Promoter));
            
            // UNKNOWN COMPONENT
            Component ins_SarJ = createComponentWithSequence("SarJInsulator", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(URI.create("http://identifiers.org/so/SO:0000627")));

            Component rbs_bm3r1 = createComponentWithSequence("bm3r1_rbs", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.RBS));
            Component cds_BM3R1 = createComponentWithSequence("bm3R1", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.CDS));
            Component ter_2 = createComponentWithSequence("ter2", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Terminator));
            
            Component prm_PSrpR = createComponentWithSequence("pSrpR", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Promoter));
            
            Component prm_PBM3R1 = createComponentWithSequence("pBM3R1", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Promoter));
            // UNKNOWN COMPONENT
            Component ins_LtsvJ = createComponentWithSequence("LtsvJ_insulator", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(URI.create("http://identifiers.org/so/SO:0000627")));

            Component phlfRBS = createComponentWithSequence("phlfRBS", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.RBS));
            Component cds_PhlF = createComponentWithSequence("phlf", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.CDS));
            
            // CHOICE 1
            //T7Te terminator or rrnB T1 terminator???
            //Component ter_3 = createComponentWithSequence("ter3", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Terminator));
            Component T7Te_terminator = createComponentWithSequence("T7Te terminator", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Terminator));
           
            Component prm_PPhIF = createComponentWithSequence("pPhlF", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Promoter));
           
            Component int2_RBS = createComponentWithSequence("int2_RBS", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.RBS));
            Component cds_Int2 = createComponentWithSequence("int2", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.CDS));
            Component ter_4 = createComponentWithSequence("ter4", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Terminator));
            
            // CHOICE 2
            //Component ter_3 = SBOLAPI.createDnaComponent(doc, "ter3", "ter3_terminator", "terminator ter3", Role.Terminator, "");
            //Component prm_PPhIF = SBOLAPI.createDnaComponent(doc, "PPhIF", "PPhIF", "PhIF repressible promoter", Role.Promoter, "");
            //Component rbs_5 = SBOLAPI.createDnaComponent(doc, "rbs5", "rbs5", "rbs5", Role.RBS, "");
            //Component cds_YFP = SBOLAPI.createDnaComponent(doc, "YFP", "YFP_CDS", "YFP coding sequence", Role.CDS, "");
            //Component ter_4 = SBOLAPI.createDnaComponent(doc, "ter4", "ter4_terminator", "terminator ter4", Role.Terminator, "");

            // IPTG and aTC
            Component IPTG = createComponentWithSequence("IPTG", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Effector));
            Component aTC = createComponentWithSequence("aTC", newDocument, convertedDocument, device, URI_prefix, Arrays.asList(Role.Effector));


            // INTERACTIONS
            //device.createInteraction(null)
            

            //SBOLAPI.createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, PTet, Arrays.asList(ParticipationRole.Inhibited.getUri()), tetR, Arrays.asList(ParticipationRole.Inhibitor.getUri()));  
            
            List<Interaction> interaction_LacI_PTac = SBOLAPI.createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, cds_LacI, Arrays.asList(ParticipationRole.Inhibitor.getUri()), prm_PTac, Arrays.asList(ParticipationRole.Inhibited.getUri()));
            List<Interaction> interaction_TetR_PTet = SBOLAPI.createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, cds_TetR, Arrays.asList(ParticipationRole.Inhibitor.getUri()), prm_PTet, Arrays.asList(ParticipationRole.Inhibited.getUri()));
            List<Interaction> interaction_SrpR_PSrpR = SBOLAPI.createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, cds_SrpR, Arrays.asList(ParticipationRole.Inhibitor.getUri()), prm_PSrpR, Arrays.asList(ParticipationRole.Inhibited.getUri()));
            List<Interaction> interaction_BM3R1_PBM3R1 = SBOLAPI.createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, cds_BM3R1, Arrays.asList(ParticipationRole.Inhibitor.getUri()), prm_PBM3R1, Arrays.asList(ParticipationRole.Inhibited.getUri()));
            // CHOICE 1
            List<Interaction> interaction_PhIF_PPhIF = SBOLAPI.createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, cds_PhlF, Arrays.asList(ParticipationRole.Inhibitor.getUri()), prm_PPhIF, Arrays.asList(ParticipationRole.Inhibited.getUri()));
            // CHOICE 2
            // Essentially same

            // Interactionception
            // Interaction to interaction is not possible in SBOL3 yet.
            // Should we also create the protein explicitly?
            //Interaction interaction_IPTG_LacI_binding = device.createInteraction("IPTG_LacI", InteractionType.Inhibition.getUri());
             

           // .createInteraction(Arrays.asList(InteractionType.Inhibition.getUri()), device, IPTG, Arrays.asList(ParticipationRole.Inhibitor.getUri()), interaction_LacI_PTac.get(0), Arrays.asList(ParticipationRole.Inhibited.getUri()));

            //Component c = doc.getIdentified(null, Component.class);
        
            // TODO: DO THIS LATER FOR NON FOUND COMPONENTS
            //SubComponent sc_ter_0=SBOLAPI.getSubComponents(device, ter_0).get(0);
            //device.createConstraint(SequentialRestriction.finishes,sc_ter_0,cds_TetR);
            //device.createConstraint(SequentialRestriction.precedes,sc_ter_0,pTeT);
            


            String output=SBOLIO.write(newDocument, SBOLFormat.TURTLE);
            //Write the newDocument to a file
            SBOLIO.write(newDocument, new File("output/datacuration/paper_sbol3_recreated.ttl"), SBOLFormat.TURTLE);
            //System.out.print(output);
            
    }


    private Component createComponentWithSequence(String name, SBOLDocument newDocument, 
        SBOLDocument convertedDocument, Component device, String uriPrefix, List<URI> roleList) throws SBOLGraphException {
        

        // TODO: FIRST FIND SEQUENCE FEATURE SEQUENCES AND COMPARE SEQUENCES IF THEY SAME THEN DO NOT CREATE NEW COMPONENT, JUST RETURN THE EXISTING ONE. OTHERWISE CREATE A NEW COMPONENT WITH THE SAME NAME BUT DIFFERENT SEQUENCE.
        // If the component with the given name already exists in the new document, dont create a new one, just return the existing one
        Component comp = createOrFindComponent(name, newDocument, roleList);


        /* 
        Component comp = newDocument.createComponent(name.replaceAll("\\s+","") , Arrays.asList(ComponentType.DNA.getUri()));
        comp.setName(name);
        comp.setRoles(roleList);
        */
        //SequenceFeature sf = convertedDocument.getIdentified(, SequenceFeature.class);
        SequenceFeature sf = findSequenceFeatureByName(convertedDocument, device.getUri(), name);
        
        
        if(sf == null) {
            notFoundCounter++;
            System.out.println(name + " - NOT FOUND :" + notFoundCounter);
            SubComponent sc = device.createSubComponent(comp.getDisplayId(), comp);
            return comp; // Return the component without sequence if no matching SequenceFeature is found
            //return null;
        } 
        foundCounter++;
        System.out.println(foundCounter + " - Found SequenceFeature for component: " + name);
        //Sequence c_seq = newDocument.createSequence(comp.getDisplayId() + "_sequence");
        
        //return createSubCompAndNaString(newDocument, sf, convertedDocument.getSequences().get(0), comp, device);
        return createSubCompAndNaString(newDocument, sf, convertedDocument.getSequences().get(0), comp, device);
        
    }

    private Component createOrFindComponent(String name, SBOLDocument doc, List<URI> roleList) throws SBOLGraphException {
        for (Component comp : doc.getComponents()) {
            if (comp.getName().equals(name)) {
                return comp; // Return existing component if found
            }
        }
        // If not found, create a new component
        Component newComp = doc.createComponent(name.replaceAll("\\s+",""), Arrays.asList(ComponentType.DNA.getUri()));
        newComp.setName(name);
        newComp.setRoles(roleList);
        return newComp;
    }
   

    private Component createSubCompAndNaString(SBOLDocument newDocument, SequenceFeature sf, Sequence convDocSequence, Component comp, Component device) throws SBOLGraphException {
        // Extract nucleotide string from SequenceFeature's locations
        //if (sf == null) return null;
        //String elements = sequence.getElements();
        //if (elements == null) return null;
        
        //int realStart = Integer.MAX_VALUE;
        int start;
        int end = 0;
        //TODO:  Only one location exists for each SequenceFeature in the converted document
        Pair<Integer, Integer> startEnd = Location.getStartEnd(sf.getLocations());
        start=startEnd.getLeft();
        end = startEnd.getRight();
        
        //sequence.setElements(convDocSequence.getElements());
        //sequence.setEncoding(org.sbolstandard.core3.vocabulary.Encoding.NucleicAcid);
        //create new seq

        Sequence newSeq = newDocument.createSequence(comp.getDisplayId() + "_sequence");
        newSeq.setElements(convDocSequence.getElements().substring(start-1, end));
        newSeq.setEncoding(org.sbolstandard.core3.vocabulary.Encoding.NucleicAcid);

        comp.setSequences(newSeq);
        SubComponent sc = device.createSubComponent("sc_"+comp.getDisplayId(),comp);
        sc.createRange(start, end, device.getSequences().get(0));
        //sc.createRange(start, end, comp.getSequences().get(0));
        //sc.createRange(start, end, device.getSequences().get(0));
        //System.out.println("Created subcomponent for " + comp.getName() + " with range: " + start + "-" + end+ " - "+ comp.getSequences().size());
        return comp;
        
    }

    
    private SequenceFeature findSequenceFeatureByName(SBOLDocument convertedDoc, URI deviceUri, String name) throws SBOLGraphException {
        Component mainCompConvDoc = convertedDoc.getIdentified(deviceUri, Component.class);
        if(mainCompConvDoc == null) return null;
        for (SequenceFeature sf : mainCompConvDoc.getSequenceFeatures()) {
            if(sf.getName().equals(name)) {
                return sf;
            }
        }
        return null;
    }


   

}
