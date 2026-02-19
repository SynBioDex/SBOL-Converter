package org.sbolstandard.converter.tests.datacuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.App;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Interaction;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.InteractionType;
import org.sbolstandard.core3.vocabulary.ParticipationRole;

public class GeneticMemory_v2 {

    @Test
    public void testPaperExample() throws SBOLGraphException, IOException, SBOLValidationException, SBOLConversionException {
        String inputFileName = "pand_int2_v2.4.gb";
        String outputFileName = "pand_int2_v2.4.xml";
        String OriginalURIPrefix = "https://keele.ac.uk/scm";
        String URIPrefix = "https://keele.ac.uk/scm/updated/";

        // Send the paper.gbp file to the App main method as an argument to convert to
        // SBOL3
        String[] args = {
                "input/datacuration/" + inputFileName,
                "-l", "SBOL3",
                "-p", OriginalURIPrefix,
                "-o", "output/datacuration/" + outputFileName
        };

        App.main(args);

        SBOLDocument docFromGenBank = SBOLIO.read(new File("output/datacuration/" + outputFileName), SBOLFormat.RDFXML);
        GenBankComponentView cvConverter = new GenBankComponentView();
        String deviceId = "pAND_Int2";
        List<String> excludeFeatures = Arrays.asList("source", "pBR322ori_F", "p15A_ori", "Kan_R", "pENTR_R", "KanR", "tet_operator", "tac_promoter", "lac_operator", "LacI_R", "Tet_R", "pBRrevBam");
        
        //Simpler view with features excluded
        SBOLDocument docCompView = cvConverter.convertSequenceFeaturesToComponents(docFromGenBank, URIPrefix, deviceId, excludeFeatures);
        addInteractions(docCompView, URIPrefix, deviceId);
        printAndSave(docCompView, new File("output/datacuration/" + outputFileName + "_compview_simple.ttl"), cvConverter);
        
        //All features are converted to components
        SBOLDocument docCompView2 = cvConverter.convertSequenceFeaturesToComponents(docFromGenBank, URIPrefix, deviceId, null);
        addInteractions(docCompView2, URIPrefix, deviceId);
        printAndSave(docCompView2, new File("output/datacuration/" + outputFileName + "_compview_full.ttl"), cvConverter);

        //Kept p15A_ori and KanR
        excludeFeatures = Arrays.asList("source", "p15A_ori", "Kan_R", "pENTR_R", "tet_operator", "tac_promoter", "lac_operator", "LacI_R", "Tet_R", "pBRrevBam");
        SBOLDocument docCompView3 = cvConverter.convertSequenceFeaturesToComponents(docFromGenBank, URIPrefix, deviceId, excludeFeatures);
        //addInteractions(docCompView3, URIPrefix, deviceId);
        
        Component device = docCompView3.getIdentified(SBOLAPI.append(URIPrefix, deviceId), Component.class);
        Component pTet = docCompView3.getIdentified(SBOLAPI.append(URIPrefix, "pTet"), Component.class);
        Component srpr = docCompView3.getIdentified(SBOLAPI.append(URIPrefix, "srpr"), Component.class);
        Component T7Te_terminator = docCompView3.getIdentified(SBOLAPI.append(URIPrefix, "T7Te_terminator"), Component.class);
        Component pTac = docCompView3.getIdentified(SBOLAPI.append(URIPrefix, "pTac"), Component.class);
        Component device3 = docCompView3.getIdentified(SBOLAPI.append(URIPrefix, deviceId), Component.class);
        
        int start =-1;
        int end= -1;
        List<SubComponent> subComponents = SBOLAPI.getSubComponents(device3, pTet);
        if (subComponents!=null){
            SubComponent subComp = subComponents.get(0);
            start = Location.getStartEnd(subComp.getLocations()).getLeft();
        }
        subComponents = SBOLAPI.getSubComponents(device3, pTac);
        if (subComponents!=null){
            SubComponent subComp = subComponents.get(0);//Use the first one for sspr
            end = Location.getStartEnd(subComp.getLocations()).getLeft()-1;
        }

        Component ssprSubDevice = SBOLDesignUtil.extractComponent(docCompView3, device3, start, end);
        /*for (SubComponent sc:device3.getSubComponents()){
            System.out.println(sc.getUri());
        }*/
        System.out.println("Before interactions:" + device3.getSubComponents().size());               
        addInteractions(docCompView3, URIPrefix, deviceId);
        System.out.println("After interactions:" + device3.getSubComponents().size());               
        
        printAndSave(docCompView3, new File("output/datacuration/" + outputFileName + "_compview_subcomponents.ttl"), cvConverter);
        
    }

    private void printAndSave(SBOLDocument docCompView, File outputFile, GenBankComponentView cvConverter) throws SBOLGraphException, IOException {
        System.out.println("********************************");
        System.out.println("File: " + outputFile.getName());
        //cvConverter.printSimpleSubComponentStructure(docCompView);
        //cvConverter.printOrderedComponents(docCompView);
        cvConverter.printOrderedRegions(docCompView);
        
        SBOLIO.write(docCompView, outputFile, SBOLFormat.TURTLE);
        System.out.println("###############################");
    }

    private void addInteractions(SBOLDocument docCompView, String URIPrefix, String deviceId) throws SBOLGraphException {
        Component device = docCompView.getIdentified(SBOLAPI.append(URIPrefix, deviceId), Component.class);
        Component lacI = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "lacI"), Component.class);
        Component pTac = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pTac"), Component.class);
        Component tetR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "tetR"), Component.class);
        Component pTet = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pTet"), Component.class);
        Component srpR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "srpr"), Component.class);
        Component pSrpR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pSrpR"), Component.class);
        Component bm3r1 = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "bm3R1"), Component.class);
        Component pBm3r1 = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pBM3R1"), Component.class);
        Component phlF = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "phlf"), Component.class);
        Component pPhlF = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pPhlF"), Component.class);

        List<Interaction> interaction_LacI_PTac = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, lacI,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pTac,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_TetR_PTet = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, tetR,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pTet,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_SrpR_PSrpR = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, srpR,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pSrpR,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_BM3R1_PBM3R1 = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, bm3r1,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pBm3r1,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_PhIF_PPhIF = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, phlF,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pPhlF,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
    }

     private void addParentInteractions(SBOLDocument docCompView, String URIPrefix, String deviceId) throws SBOLGraphException {
        Component device = docCompView.getIdentified(SBOLAPI.append(URIPrefix, deviceId), Component.class);
        Component lacI = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "lacI"), Component.class);
        Component pTac = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pTac"), Component.class);
        Component tetR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "tetR"), Component.class);
        Component pTet = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pTet"), Component.class);
        Component srpR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "srpr"), Component.class);
        Component pSrpR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pSrpR"), Component.class);
        Component bm3r1 = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "bm3R1"), Component.class);
        Component pBm3r1 = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pBM3R1"), Component.class);
        Component phlF = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "phlf"), Component.class);
        Component pPhlF = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pPhlF"), Component.class);

        List<Interaction> interaction_LacI_PTac = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, lacI,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pTac,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_SrpR_PSrpR = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, srpR,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pSrpR,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_BM3R1_PBM3R1 = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, bm3r1,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pBm3r1,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
        List<Interaction> interaction_PhIF_PPhIF = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, phlF,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pPhlF,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
    }

  

     private void add_srpr_Production_Interaction(SBOLDocument docCompView, String URIPrefix, String deviceId) throws SBOLGraphException {
        Component device = docCompView.getIdentified(SBOLAPI.append(URIPrefix, deviceId), Component.class);
        Component tetR = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "tetR"), Component.class);
        Component pTet = docCompView.getIdentified(SBOLAPI.append(URIPrefix, "pTet"), Component.class);
        
        List<Interaction> interaction_TetR_PTet = SBOLAPI.createInteraction(
                Arrays.asList(InteractionType.Inhibition.getUri()), device, tetR,
                Arrays.asList(ParticipationRole.Inhibitor.getUri()), pTet,
                Arrays.asList(ParticipationRole.Inhibited.getUri()));
    }


}
