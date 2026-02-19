package org.sbolstandard.converter.tests.datacuration;

import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Range;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.SBOLUtil;
import org.sbolstandard.core3.util.URINameSpace;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Orientation;
import org.sbolstandard.core3.vocabulary.RestrictionType;
import org.sbolstandard.core3.vocabulary.Role;

/**
 * GenBankComponentView converts SequenceFeatures to SubComponents.
 * Takes an SBOLDocument with a single device and multiple features,
 * creates a new document with Components for each sequence feature,
 * reusing existing Components when sequences are identical.
 */
public class GenBankComponentView {
    
    private Map<String, Component> sequenceToComponentMap;
    
    public GenBankComponentView() {
        this.sequenceToComponentMap = new HashMap<>();
    }
    
    /**
     * Converts an SBOL document with sequence features to a new document
     * with components and subcomponents.
     * 
     * @param inputDocument The input SBOL document containing a device with sequence features
     * @param uriPrefix The URI prefix for the output document
     * @param displayId The display ID for the output device
     * @return A new SBOL document with converted components and subcomponents
     * @throws SBOLGraphException
     */
    public SBOLDocument convertSequenceFeaturesToComponents(SBOLDocument inputDocument, String uriPrefix, String displayId)
            throws SBOLGraphException {
        return convertSequenceFeaturesToComponents(inputDocument, uriPrefix, displayId, null);
    }
    
    /**
     * Converts an SBOL document with sequence features to a new document
     * with components and subcomponents.
     * 
     * @param inputDocument The input SBOL document containing a device with sequence features
     * @param uriPrefix The URI prefix for the output document
     * @param displayId The display ID for the output device
     * @param excludeFeatures List of feature names or displayIds to exclude from conversion
     * @return A new SBOL document with converted components and subcomponents
     * @throws SBOLGraphException
     */
    public SBOLDocument convertSequenceFeaturesToComponents(SBOLDocument inputDocument, String uriPrefix, String displayId, List<String> excludeFeaturePrefixes) throws SBOLGraphException{
        // Clear the sequence-to-component mapping for each conversion
        sequenceToComponentMap.clear();
        
        // Create new output document
        SBOLDocument outputDocument = new SBOLDocument(URI.create(uriPrefix));
        
        // Find the main device component (assuming single device as per requirements)
        Component inputDevice = getMainDevice(inputDocument);
        if (inputDevice == null) {
            throw new IllegalArgumentException("No device component found in input document");
        }
        
        // Create corresponding device component in output document
        Component outputDevice = createDeviceComponent(outputDocument, inputDevice, displayId);
        
        // Get the main sequence from input document
        Sequence mainSequence = getMainSequence(inputDocument, inputDevice);
        if (mainSequence == null) {
            throw new IllegalArgumentException("No sequence found for the main device");
        }
        
        // Process each sequence feature and convert to component/subcomponent
        List<SequenceFeature> sequenceFeatures = inputDevice.getSequenceFeatures();
        if (sequenceFeatures != null) {
            for (SequenceFeature sequenceFeature : sequenceFeatures) {
                processSequenceFeature(sequenceFeature, mainSequence, outputDocument, outputDevice, uriPrefix, excludeFeaturePrefixes);
            }
        }
        
        return outputDocument;
    }
    
    /**
     * Checks if a component should be excluded from conversion based on its generated ID and name.
     * 
     * @param componentId The generated component ID to check
     * @param componentName The component name to check
     * @param excludeFeaturePrefixes List of component ID/name prefixes to exclude
     * @return true if the component should be excluded, false otherwise
     */
    private boolean shouldExcludeComponent(String componentId, String componentName, List<String> excludeFeaturePrefixes) {
        if (excludeFeaturePrefixes == null || excludeFeaturePrefixes.isEmpty()) {
            return false;
        }
        
        String lcaseId = componentId != null ? componentId.toLowerCase() : "";
        String lcaseName = componentName != null ? componentName.toLowerCase() : "";
        
        // Check if componentId or componentName starts with any exclude pattern
        for (String excludePattern : excludeFeaturePrefixes) {
            if (excludePattern == null || excludePattern.isEmpty()) {
                continue;
            }
            
            String lowerExcludePattern = excludePattern.toLowerCase();            
            // Check if componentId starts with the exclude pattern
            if (!lcaseId.isEmpty() && lcaseId.startsWith(lowerExcludePattern)) {
                return true;
            }
            // Check if componentName starts with the exclude pattern
            else if (!lcaseName.isEmpty() && lcaseName.startsWith(lowerExcludePattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the main device component from the input document.
     * Assumes the first component is the main device.
     */
    private Component getMainDevice(SBOLDocument document) throws SBOLGraphException {
        List<Component> components = document.getComponents();
        return (components != null && !components.isEmpty()) ? components.get(0) : null;
    }
    
    /**
     * Creates the device component in the output document corresponding to the input device.
     */
    private Component createDeviceComponent(SBOLDocument outputDocument, Component inputDevice,String displayId) 
            throws SBOLGraphException {
        
       Component device = outputDocument.createComponent(displayId, Arrays.asList(ComponentType.DNA.getUri()));
        
        if (inputDevice.getName() != null) {
            device.setName(inputDevice.getName());
        }
        
        if (inputDevice.getDescription() != null) {
            device.setDescription(inputDevice.getDescription());
        }
        
        // Set appropriate role for DNA device to satisfy SBOL validation
        List<URI> inputRoles = inputDevice.getRoles();
        if (inputRoles != null && !inputRoles.isEmpty()) {
            device.setRoles(inputRoles);
        } else {
            device.setRoles(Arrays.asList(Role.EngineeredRegion));
        }
        
        // Copy the main sequence to the output device
        List<Sequence> inputSequences = inputDevice.getSequences();
        if (inputSequences != null && !inputSequences.isEmpty()) {
            Sequence inputSeq = inputSequences.get(0);
            Sequence outputSeq = outputDocument.createSequence(displayId + "_sequence");
            outputSeq.setElements(inputSeq.getElements());
            outputSeq.setEncoding(inputSeq.getEncoding());
            device.addSequence(outputSeq);
        }
        
        return device;
    }
    
    /**
     * Gets the main sequence associated with the device component.
     */
    private Sequence getMainSequence(SBOLDocument document, Component device) throws SBOLGraphException {
        List<Sequence> sequences = device.getSequences();
        if (sequences != null && !sequences.isEmpty()) {
            return sequences.get(0);
        }
        
        // If no sequence directly on the device, try to find one in the document
        List<Sequence> docSequences = document.getSequences();
        return (docSequences != null && !docSequences.isEmpty()) ? docSequences.get(0) : null;
    }
    
    /**
     * Processes a single sequence feature and converts it to a component/subcomponent.
     */
    private void processSequenceFeature(SequenceFeature sequenceFeature, Sequence mainSequence, 
            SBOLDocument outputDocument, Component outputDevice, String uriPrefix, List<String> excludeFeaturePrefixes) throws SBOLGraphException {
        
        // Extract the nucleotide sequence for this feature
        String featureSequence = extractFeatureSequence(sequenceFeature, mainSequence);
        if (featureSequence == null || featureSequence.isEmpty()) {
            return; // Skip features without valid sequences
        }
        if (sequenceFeature.getOrientation() != null && sequenceFeature.getOrientation()==Orientation.reverseComplement) {
            featureSequence = SBOLDesignUtil.getReverseComplement(featureSequence);
        }
        // Check if we already have a component for this sequence
        Component featureComponent = sequenceToComponentMap.get(featureSequence.toLowerCase());
        
        if (featureComponent == null) {
            // Create new component for this sequence
            featureComponent = createFeatureComponent(sequenceFeature, featureSequence, outputDocument, uriPrefix, excludeFeaturePrefixes);
            if (featureComponent == null) {
                return; // Feature was excluded
            }
            sequenceToComponentMap.put(featureSequence.toLowerCase(), featureComponent);
        }
        
        // Create subcomponent in the output device
        createSubComponent(sequenceFeature, featureComponent, outputDevice, mainSequence);
    }
    
    /**
     * Extracts the nucleotide sequence string for a sequence feature.
     */
    private String extractFeatureSequence(SequenceFeature sequenceFeature, Sequence mainSequence) 
            throws SBOLGraphException {
        
        if (sequenceFeature.getLocations() == null || sequenceFeature.getLocations().isEmpty()) {
            return null;
        }
        
        // Get start and end positions from the sequence feature locations
        Pair<Integer, Integer> startEnd = Location.getStartEnd(sequenceFeature.getLocations());
        if (startEnd == null) {
            return null;
        }
        
        int start = startEnd.getLeft();
        int end = startEnd.getRight();
        
        String mainElements = mainSequence.getElements();
        if (mainElements == null || start < 1 || end > mainElements.length()) {
            return null;
        }
        
        // Extract substring (convert from 1-based to 0-based indexing)
        return mainElements.substring(start - 1, end);
    }
    
    /**
     * Creates a new component for a sequence feature.
     */
    private Component createFeatureComponent(SequenceFeature sequenceFeature, String featureSequence, 
            SBOLDocument outputDocument, String uriPrefix, List<String> excludeFeaturePrefixes) throws SBOLGraphException {
        
        String componentId = generateComponentId(sequenceFeature, outputDocument);
        
        // Check if this component should be excluded based on the generated component ID
        if (excludeFeaturePrefixes != null && shouldExcludeComponent(componentId, sequenceFeature.getName(), excludeFeaturePrefixes)) {
            return null; // Exclude this component
        }
        
        Component component = outputDocument.createComponent(componentId, Arrays.asList(ComponentType.DNA.getUri()));
        component.setWasDerivedFrom(Arrays.asList(sequenceFeature.getUri()));
        // Set name from sequence feature
        if (sequenceFeature.getName() != null) {
            component.setName(sequenceFeature.getName());
        }
        
        // Set description 
        if (sequenceFeature.getDescription() != null) {
            component.setDescription(sequenceFeature.getDescription());
        }
        
        // Copy roles if they exist
        List<URI> featureRoles = sequenceFeature.getRoles();
        if (featureRoles != null && !featureRoles.isEmpty()) {
            component.setRoles(featureRoles);
        }
        
        // Create and attach sequence
        Sequence componentSequence = outputDocument.createSequence(componentId + "_sequence");
        componentSequence.setElements(featureSequence);
        componentSequence.setEncoding(org.sbolstandard.core3.vocabulary.Encoding.NucleicAcid);
        component.addSequence(componentSequence);
        
        return component;
    }
    
    /**
     * Generates a unique component ID from a sequence feature.
     * First tries to use the feature name, and if already used, appends the displayId.
     */
    private String generateComponentId(SequenceFeature sequenceFeature, SBOLDocument outputDocument) throws SBOLGraphException {
        // Start with the feature name
        String baseId = sequenceFeature.getName();
        
        // If name is null or empty, use displayId as fallback
        if (baseId == null || baseId.isEmpty()) {
            baseId = sequenceFeature.getDisplayId();
        }
        
        // Clean up the ID to make it valid
        baseId = baseId.replaceAll("[^a-zA-Z0-9_]", "_");
        
        // Ensure it starts with a letter or underscore
        if (!baseId.matches("^[a-zA-Z_].*")) {
            baseId = "comp_" + baseId;
        }
                
        // Check if this ID is already used by another component
        if (isComponentIdInUse(baseId, outputDocument)) {
            // Append the displayId to make it unique
            String displayId = sequenceFeature.getDisplayId();
            if (displayId != null && !displayId.isEmpty()) {
                baseId = baseId + "_" + displayId;
            } 
        }
        
        return baseId;
    }
    
    /**
     * Checks if a component ID is already in use in the output document.
     */
    private boolean isComponentIdInUse(String componentId, SBOLDocument outputDocument) throws SBOLGraphException {
        List<Component> components = outputDocument.getComponents();
        if (components != null) {
            for (Component comp : components) {
                if (componentId.equals(comp.getDisplayId())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Creates a subcomponent in the output device referencing the feature component.
     */
    private void createSubComponent(SequenceFeature sequenceFeature, Component featureComponent, 
            Component outputDevice, Sequence mainSequence) throws SBOLGraphException {
        
        int index=1;
        if (outputDevice.getSubComponents()!=null) {
            index=outputDevice.getSubComponents().size()+1;
        }
        String subComponentId = "sc_" + featureComponent.getDisplayId() + "_" + index;
        SubComponent subComponent = outputDevice.createSubComponent(subComponentId, featureComponent);
        subComponent.setOrientation(sequenceFeature.getOrientation());
        // Add location information to the subcomponent
        List<Location> locations = sequenceFeature.getLocations();
        if (locations != null && !locations.isEmpty()) {
            // Get the device's sequence for creating locations
            List<Sequence> deviceSequences = outputDevice.getSequences();
            if (deviceSequences != null && !deviceSequences.isEmpty()) {
                Sequence deviceSequence = deviceSequences.get(0);
                
                // Create a location for each source location
                for (Location originalLocation : locations) {
                    if (originalLocation instanceof Range) {
                        Range originalRange = (Range) originalLocation;
                        int start = originalRange.getStart().get();
                        int end = originalRange.getEnd().get();
                        
                        // Create corresponding range on the device's sequence
                        Range newRange = subComponent.createRange(start, end, deviceSequence);
                        
                        // Convert orientation from the original location
                        if (originalRange.getOrientation() != null) {
                            newRange.setOrientation(originalRange.getOrientation());
                        }
                    } else if (originalLocation instanceof org.sbolstandard.core3.entity.Cut) {
                        org.sbolstandard.core3.entity.Cut originalCut = (org.sbolstandard.core3.entity.Cut) originalLocation;
                        int at = originalCut.getAt().get();
                        
                        // Create corresponding cut on the device's sequence
                        org.sbolstandard.core3.entity.Cut newCut = subComponent.createCut(at, deviceSequence);
                        
                        // Convert orientation from the original location
                        if (originalCut.getOrientation() != null) {
                            newCut.setOrientation(originalCut.getOrientation());
                        }
                    } else if (originalLocation instanceof org.sbolstandard.core3.entity.EntireSequence) {
                        org.sbolstandard.core3.entity.EntireSequence originalEntireSeq = (org.sbolstandard.core3.entity.EntireSequence) originalLocation;
                        
                        // Create corresponding entire sequence on the device's sequence
                        org.sbolstandard.core3.entity.EntireSequence newEntireSeq = subComponent.createEntireSequence(deviceSequence);
                        
                        // Convert orientation from the original location
                        if (originalEntireSeq.getOrientation() != null) {
                            newEntireSeq.setOrientation(originalEntireSeq.getOrientation());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns statistics about the conversion process.
     */
    public Map<String, Integer> getConversionStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("uniqueComponents", sequenceToComponentMap.size());
        return stats;
    }
    
    /**
     * Clears the internal sequence-to-component mapping.
     */
    public void clearCache() {
        sequenceToComponentMap.clear();
    }
    
    /**
     * Test method demonstrating usage of GenBankComponentView.
     * Creates a sample input document and converts it using the GenBankComponentView.
     */
    @Test
    public void testGenBankComponentViewConversion() throws SBOLGraphException, IOException {
        // Create a sample input document for testing
        SBOLDocument inputDocument = createSampleInputDocument();
        
        // Create GenBankComponentView and perform conversion
        GenBankComponentView converter = new GenBankComponentView();
        String uriPrefix = "https://example.com/test/";
        SBOLDocument outputDocument = converter.convertSequenceFeaturesToComponents(inputDocument, uriPrefix, "test_device_converted");
        
        // Print conversion statistics
        Map<String, Integer> stats = converter.getConversionStats();
        System.out.println("Conversion completed successfully!");
        System.out.println("Number of unique components created: " + stats.get("uniqueComponents"));
        System.out.println("Input document had " + getSequenceFeatureCount(inputDocument) + " sequence features");
        System.out.println("Output document has " + outputDocument.getComponents().size() + " components");
        
        // Expected results for validation:
        // - 8 sequence features in input
        // - 4 unique DNA sequences (promoter, RBS, CDS, terminator appear twice, but different CDS appears once)
        // - Should create 5 unique components: promoter, RBS, CDS1, terminator, CDS2
        // - Plus 1 device component = 6 total components
        System.out.println("Expected: 8 sequence features -> 5 unique feature components + 1 device = 6 total components");
        System.out.println("Expected: 4 components should be reused (promoter, RBS, terminator each used twice)");
        
        // Write output to file for inspection
        File outputFile = new File("output/datacuration/genbank_component_view_output.xml");
        outputFile.getParentFile().mkdirs(); // Create directories if they don't exist
        
        try {
            SBOLIO.write(outputDocument, outputFile, SBOLFormat.RDFXML);
            System.out.println("Output written to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Warning: Could not write file: " + e.getMessage());
            // Print the document to console instead
            try {
                String output = SBOLIO.write(outputDocument, SBOLFormat.TURTLE);
                System.out.println("Document content (TURTLE format):");
                System.out.println(output.substring(0, Math.min(500, output.length())) + "...");
            } catch (Exception e2) {
                System.out.println("Could not write document: " + e2.getMessage());
            }
        }
        
        // Optional: Print document structure
        printDocumentStructure(outputDocument);
        SBOLIO.write(outputDocument, System.out, SBOLFormat.TURTLE);
        printOrderedComponents(outputDocument);
        System.out.println(inputDocument.getComponents().get(0).getSequenceFeatures().size());
        System.out.println(outputDocument.getComponents().size());

        Component sampleDevice=outputDocument.getIdentified(URI.create("https://example.com/test/test_device_converted"), Component.class);
        Component cdsFeature=outputDocument.getIdentified(URI.create("https://example.com/test/GFP_CDS"), Component.class);
        Component rbsFeature=outputDocument.getIdentified(URI.create("https://example.com/test/RBS"), Component.class);
        
        //SBOLDesignUtil.extractComponent(outputDocument, sampleDevice, cdsFeature, rbsFeature);
        SBOLDesignUtil.extractComponent(outputDocument, sampleDevice, 21, 50);
        
        printDocumentStructure(outputDocument);
        printOrderedComponents(outputDocument);
        //org.sbolstandard.core3.util.Configuration.getInstance().setValidateBeforeSaving(false);
        SBOLIO.write(outputDocument, System.out, SBOLFormat.TURTLE);

        SBOLDesignUtil.extractComponent(outputDocument, sampleDevice, 21, 120);
        
        printOrderedComponents(outputDocument);
        printOrderedRegions(outputDocument);
        
    }
    
    /**
     * Creates a sample input document with a device component containing sequence features.
     */
    private SBOLDocument createSampleInputDocument() throws SBOLGraphException {
        String uriPrefix = "https://example.com/input/";
        SBOLDocument doc = new SBOLDocument(URI.create(uriPrefix));
        
        // Create main device component with proper SO role
        Component device = doc.createComponent("sample_device", Arrays.asList(ComponentType.DNA.getUri()));
        device.setName("Sample Genetic Device");
        device.setDescription("A sample genetic device with multiple sequence features");
        device.setRoles(Arrays.asList(Role.EngineeredRegion));
        
        // Create main sequence with repeated sequences to test component reuse
        Sequence mainSequence = doc.createSequence("sample_device_sequence");
        // Design sequence with repeated promoter and RBS sequences for testing reuse
        String promoterSeq = "ATGGCTAGCGAATTCCTGCA";     // 20bp promoter sequence
        String rbsSeq = "AGGAGGTTAA";                     // 10bp RBS sequence  
        String cdsSeq = "ATGAAACGCATTGATGCACC";          // 20bp CDS sequence
        String terminatorSeq = "TTTTTTGCGC";              // 10bp terminator sequence
        String spacerSeq = "NNNNNNNNNN";                  // 10bp spacer
        String reversePromoterSeq ="TGCAGGAATTCGCTAGCCAT";
        // Construct sequence: promoter + RBS + CDS + terminator + spacer + promoter(again) + RBS(again) + different_CDS + terminator
        String dnaSequence = promoterSeq + rbsSeq + cdsSeq + terminatorSeq + spacerSeq + 
                           promoterSeq + rbsSeq + "ATGCCCGAAATTGATGCACC" + terminatorSeq + reversePromoterSeq;
        // Total length: 20+10+20+10+10+20+10+20+10 = 130bp
//ATGGCTAGCGAATTCCTGCAAGGAGGTTAAATGAAACGCATTGATGCACC      
        mainSequence.setElements(dnaSequence);
        mainSequence.setEncoding(org.sbolstandard.core3.vocabulary.Encoding.NucleicAcid);
        device.addSequence(mainSequence);
        
        // Create sequence features
        
        // Feature 1: First promoter (positions 1-20)
        SequenceFeature promoter = device.createSequenceFeature("promoter_feature");
        promoter.setName("pLac Promoter");
        promoter.setDescription("Lac promoter sequence");
        promoter.setRoles(Arrays.asList(Role.Promoter));
        promoter.createRange(1, 20, mainSequence);
        
        // Feature 2: First RBS (positions 21-30) 
        SequenceFeature rbs = device.createSequenceFeature("rbs_feature");
        rbs.setName("RBS");
        rbs.setDescription("Ribosome binding site");
        rbs.setRoles(Arrays.asList(Role.RBS));
        rbs.createRange(21, 30, mainSequence);
        
        // Feature 3: First CDS (positions 31-50)
        SequenceFeature cds = device.createSequenceFeature("cds_feature");
        cds.setName("GFP CDS");
        cds.setDescription("Green Fluorescent Protein coding sequence");
        cds.setRoles(Arrays.asList(Role.CDS));
        cds.createRange(31, 50, mainSequence);
        
        // Feature 4: First terminator (positions 51-60)
        SequenceFeature terminator1 = device.createSequenceFeature("terminator1_feature");
        terminator1.setName("Terminator");
        terminator1.setDescription("Transcription terminator");
        terminator1.setRoles(Arrays.asList(Role.Terminator));
        terminator1.createRange(51, 60, mainSequence);
        
        // Feature 5: Second promoter with SAME sequence as Feature 1 (positions 71-90) - TESTS REUSE
        SequenceFeature promoter2 = device.createSequenceFeature("promoter2_feature");
        promoter2.setName("pLac Promoter Copy");
        promoter2.setDescription("Another copy of Lac promoter sequence - should reuse component");
        promoter2.setRoles(Arrays.asList(Role.Promoter));
        promoter2.createRange(71, 90, mainSequence); // Same sequence as positions 1-20
        
        // Feature 6: Second RBS with SAME sequence as Feature 2 (positions 91-100) - TESTS REUSE  
        SequenceFeature rbs2 = device.createSequenceFeature("rbs2_feature");
        rbs2.setName("RBS Copy");
        rbs2.setDescription("Another copy of RBS sequence - should reuse component");
        rbs2.setRoles(Arrays.asList(Role.RBS));
        rbs2.createRange(91, 100, mainSequence); // Same sequence as positions 21-30
        
        // Feature 7: Different CDS (positions 101-120) - should NOT reuse
        SequenceFeature cds2 = device.createSequenceFeature("cds2_feature");
        cds2.setName("Different CDS");
        cds2.setDescription("Different coding sequence - should create new component");
        cds2.setRoles(Arrays.asList(Role.CDS));
        cds2.createRange(101, 120, mainSequence);
        
        // Feature 8: Second terminator with SAME sequence as Feature 4 (positions 121-130) - TESTS REUSE
        SequenceFeature terminator2 = device.createSequenceFeature("terminator2_feature");
        terminator2.setName("Terminator Copy");
        terminator2.setDescription("Another copy of terminator sequence - should reuse component");
        terminator2.setRoles(Arrays.asList(Role.Terminator));
        terminator2.createRange(121, 130, mainSequence); // Same sequence as positions 51-60

        // Feature 9: Reverse promoter (positions 131-150) - TESTS REUSE
        SequenceFeature revPromoter = device.createSequenceFeature("revPromoter");
        revPromoter.setName("Reverse Promoter");
        revPromoter.setDescription("Another copy of reverse promoter sequence - should reuse component");
        revPromoter.setRoles(Arrays.asList(Role.Promoter));
        revPromoter.createRange(131, 150, mainSequence); // Same sequence as positions 131-150
        revPromoter.setOrientation(Orientation.reverseComplement);


        return doc;
    }
    
    /**
     * Counts the number of sequence features in a document.
     */
    private int getSequenceFeatureCount(SBOLDocument document) throws SBOLGraphException {
        int count = 0;
        List<Component> components = document.getComponents();
        if (components != null) {
            for (Component comp : components) {
                List<SequenceFeature> features = comp.getSequenceFeatures();
                if (features != null) {
                    count += features.size();
                }
            }
        }
        return count;
    }
    
    /**
     * Prints the structure of the output document for debugging.
     */
    public void printDocumentStructure(SBOLDocument document) throws SBOLGraphException {
        
        System.out.println("\n=== Output Document Structure ===");
        
        List<Component> components = document.getComponents();
        if (components != null) {
            for (Component comp : components) {
                System.out.println("Component: " + comp.getName() + " (" + comp.getDisplayId() + ")");
                System.out.println("  Description: " + comp.getDescription());
                System.out.println("  Roles: " + comp.getRoles());
                
                List<Sequence> sequences = comp.getSequences();
                if (sequences != null && !sequences.isEmpty()) {
                    Sequence seq = sequences.get(0);
                    System.out.println("  Sequence length: " + 
                        (seq.getElements() != null ? seq.getElements().length() : "null"));
                }
                
                List<SubComponent> subComponents = comp.getSubComponents();
                int subCompCount = (subComponents != null) ? subComponents.size() : 0;
                System.out.println("  SubComponents (" + subCompCount + "):");
                
                if (subComponents != null) {
                    for (SubComponent sc : subComponents) {
                        Component instanceOf = sc.getInstanceOf();
                        System.out.println("    - " + sc.getDisplayId() + 
                            " (instance of: " + (instanceOf != null ? instanceOf.getName() : "null") + ")");
                        
                        List<Location> locations = sc.getLocations();
                        if (locations != null && !locations.isEmpty()) {
                            Pair<Integer, Integer> range = Location.getStartEnd(locations);
                            if (range != null) {
                                System.out.println("      Range: " + range.getLeft() + "-" + range.getRight());
                            }
                        }
                    }
                }
                System.out.println();
            }
        }
    }
    
    /**
     * Prints a simplified document structure using only display IDs with tab indentation.
     */
    public void printSimpleSubComponentStructure(SBOLDocument document) throws SBOLGraphException {
        System.out.println("\n=== Simple Document Structure ===");
        
        List<Component> components = document.getComponents();
        if (components != null) {
            for (Component comp : components) {
                System.out.println(comp.getDisplayId());
                printSubComponentsRecursively(comp, 1);
            }
        }
    }
    
    /**
     * Recursively prints subcomponents with proper indentation.
     */
    private void printSubComponentsRecursively(Component component, int indentLevel) throws SBOLGraphException {
        List<SubComponent> subComponents = component.getSubComponents();
        if (subComponents != null) {
            for (SubComponent sc : subComponents) {
                // Create indentation string
                StringBuilder indent = new StringBuilder();
                for (int i = 0; i < indentLevel; i++) {
                    indent.append("\t");
                }
                
                // Print the displayId of the Component that this SubComponent references
                Component instanceOf = sc.getInstanceOf();
                if (instanceOf != null) {
                    System.out.println(indent.toString() + instanceOf.getDisplayId());
                    // Recursively print subcomponents of this component
                    printSubComponentsRecursively(instanceOf, indentLevel + 1);
                }
            }
        }
    }
    
    /**
     * Prints components ordered by their location information for parents that have subcomponents.
     */
    public void printOrderedComponents(SBOLDocument document) throws SBOLGraphException {
        Map<String, List<SubComponentWithLocation>> orderedComponents = getOrderedComponents(document);
        printOrderedComponentsData(orderedComponents);
    }

      /**
     * Prints regions ordered by their location information for parents that have subcomponents.
     */
    public void printOrderedRegions(SBOLDocument document) throws SBOLGraphException {
        Map<String, List<RegionWithLocation>> orderedRegions = getOrderedFeatures2(document);
        printOrderedRegions(orderedRegions);
    }
    
    /**
     * Returns components ordered by their location information for parents that have subcomponents.
     * @return Map where keys are parent component displayIds and values are ordered lists of subcomponents
     */
    public Map<String, List<SubComponentWithLocation>> getOrderedComponents(SBOLDocument document) throws SBOLGraphException {
        Map<String, List<SubComponentWithLocation>> result = new HashMap<>();
        
        List<Component> components = document.getComponents();
        if (components != null) {
            for (Component comp : components) {
                List<SubComponent> subComponents = comp.getSubComponents();
                if (subComponents != null && !subComponents.isEmpty()) {
                    
                    // Create a list of subcomponents with their positions and orientations for sorting
                    List<SubComponentWithLocation> subCompWithLoc = new ArrayList<>();
                    
                    for (SubComponent sc : subComponents) {
                        List<Location> locations = sc.getLocations();
                        if (locations != null && !locations.isEmpty()) {
                            Component instanceOf = sc.getInstanceOf();
                            if (instanceOf != null) {
                                
                                // Count how many Range locations this subcomponent has
                                List<Range> ranges = new ArrayList<>();
                                for (Location loc : locations) {
                                    if (loc instanceof Range) {
                                        ranges.add((Range) loc);
                                    }
                                }
                                
                                // Process each Range location separately
                                for (Range rangeObj : ranges) {
                                    int start = rangeObj.getStart().get();
                                    int end = rangeObj.getEnd().get();
                                    
                                    // Check orientation
                                    URI orientation = rangeObj.getOrientation() != null ? 
                                        rangeObj.getOrientation().getUri() : Orientation.inline.getUri();
                                    boolean isReverse = (orientation != null && orientation.equals(Orientation.reverseComplement.getUri()));
                                    
                                    // Use appropriate position for sorting based on orientation
                                    int effectivePosition = isReverse ? end : start;
                                    
                                    // Format display ID based on number of ranges
                                    String displayId;
                                    if (ranges.size() > 1) {
                                        displayId = instanceOf.getDisplayId() + " [" + start + "-" + end + "]";
                                    } else {
                                        displayId = instanceOf.getDisplayId();
                                    }
                                    
                                    subCompWithLoc.add(new SubComponentWithLocation(
                                        displayId, 
                                        effectivePosition, 
                                        start, 
                                        end, 
                                        isReverse,
                                        sc,
                                        instanceOf
                                    ));
                                }
                            }
                        }
                    }
                    
                    // Sort by effective position (considering orientation)
                    subCompWithLoc.sort((a, b) -> Integer.compare(a.effectivePosition, b.effectivePosition));
                    
                    result.put(comp.getDisplayId(), subCompWithLoc);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Returns features ordered by their location information for components that have features.
     * @return Map where keys are component displayIds and values are ordered lists of features
     */
    public Map<String, List<RegionWithLocation>> getOrderedFeatures(SBOLDocument document) throws SBOLGraphException {
        Map<String, List<RegionWithLocation>> result = new HashMap<>();
        
        List<Component> components = document.getComponents();
        if (components != null) {
            for (Component comp : components) {
                List<Feature> features = comp.getFeatures();
                if (features != null && !features.isEmpty()) {
                    
                    // Create a list of features with their positions and orientations for sorting
                    List<RegionWithLocation> featuresWithLoc = new ArrayList<>();
                    
                    for (Feature feature : features) {
                        org.sbolstandard.core3.entity.FeatureWithLocation featureWithLoc = (org.sbolstandard.core3.entity.FeatureWithLocation) feature;
                        List<Location> locations = featureWithLoc.getLocations();
                        if (locations != null && !locations.isEmpty()) {
                            
                            // Count how many Range locations this feature has
                            List<Range> ranges = new ArrayList<>();
                            for (Location loc : locations) {
                                if (loc instanceof Range) {
                                    ranges.add((Range) loc);
                                }
                            }
                            
                            // Process each Range location separately
                            for (Range rangeObj : ranges) {
                                int start = rangeObj.getStart().get();
                                int end = rangeObj.getEnd().get();
                                
                                // Check orientation
                                URI orientation = rangeObj.getOrientation() != null ? 
                                    rangeObj.getOrientation().getUri() : Orientation.inline.getUri();
                                boolean isReverse = (orientation != null && orientation.equals(Orientation.reverseComplement.getUri()));
                                
                                // Use appropriate position for sorting based on orientation
                                int effectivePosition = isReverse ? end : start;
                                
                                // Format display ID based on number of ranges
                                String displayId;
                                if (ranges.size() > 1) {
                                    displayId = feature.getDisplayId() + " [" + start + "-" + end + "]";
                                } else {
                                    displayId = feature.getDisplayId();
                                }
                                
                                // Get feature name if available
                                String featureName = feature.getName();
                                String featureType = feature.getClass().getSimpleName();
                                
                                featuresWithLoc.add(new RegionWithLocation(
                                    displayId, 
                                    effectivePosition, 
                                    start, 
                                    end, 
                                    isReverse,
                                    featureName,
                                    featureType,
                                    feature.getUri(),
                                    feature
                                ));
                            }
                        }
                    }
                    
                    // Sort by effective position (considering orientation)
                    featuresWithLoc.sort((a, b) -> Integer.compare(a.effectivePosition, b.effectivePosition));
                    
                    result.put(comp.getDisplayId(), featuresWithLoc);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Gets ordered features from an SBOL document using Collections.sort with constraint-based ordering.
     * Uses sequence constraints to determine precedence relationships between features.
     * @param document the SBOL document containing features to order
     * @return map of component display IDs to their ordered features
     * @throws SBOLGraphException if there's an error accessing SBOL graph
     */
    public Map<String, List<RegionWithLocation>> getOrderedFeatures2(SBOLDocument document) throws SBOLGraphException {
        Map<String, List<RegionWithLocation>> result = new HashMap<>();
        
        for (Component comp : document.getComponents()) {
            if (comp.getFeatures() != null) {
                List<RegionWithLocation> featuresWithLoc = buildFeatureLocationList(comp);
                
                // Sort using Collections.sort with constraint-aware comparator
                Collections.sort(featuresWithLoc, new Comparator<RegionWithLocation>() {
                    @Override
                    public int compare(RegionWithLocation a, RegionWithLocation b) {
                        try {
                            return compareUsingConstraints(comp, a, b);
                        } catch (SBOLGraphException e) {
                            // If we can't access constraint data, fall back to position comparison
                            //return compareByEffectivePosition(a, b);
                            throw new RuntimeException("Error comparing features: " + e.getMessage(), e);
                        }
                    }
                });
                
                result.put(comp.getDisplayId(), featuresWithLoc);
            }
        }
        
        return result;
    }
    
    /**
     * Builds a list of FeatureWithLocation objects from a component's features.
     * @param comp the component containing features
     * @return list of features with their location data
     * @throws SBOLGraphException if there's an error accessing feature locations
     */
    private List<RegionWithLocation> buildFeatureLocationList(Component comp) throws SBOLGraphException {
        List<RegionWithLocation> features = new ArrayList<>();
        
        for (Feature feature : comp.getFeatures()) {
            int start=-1;
            int end=-1;
            if (feature instanceof org.sbolstandard.core3.entity.FeatureWithLocation) {
                org.sbolstandard.core3.entity.FeatureWithLocation featureWithLoc = (org.sbolstandard.core3.entity.FeatureWithLocation) feature;
                Pair<Integer, Integer> range = Location.getStartEnd(featureWithLoc.getLocations());
                start=range.getLeft();
                end=range.getRight();                
            }
            boolean isReverse = feature.getOrientation()!=null && feature.getOrientation().equals(Orientation.reverseComplement);
            int effectivePosition = isReverse ? end : start;   
            RegionWithLocation featureLoc = new RegionWithLocation(feature.getDisplayId(), effectivePosition, start, end, isReverse, feature.getName(), feature.getClass().getSimpleName(), feature.getUri(),feature);
            features.add(featureLoc);
        }
        
        return features;
    }
    
    /**
     * Compares two features using sequence constraints for ordering.
     * If constraints exist, uses precedence relationships; otherwise falls back to position.
     * @param comp the parent component containing the constraints
     * @param a the first feature to compare
     * @param b the second feature to compare
     * @return negative if a < b, positive if a > b, zero if equal
     */
    private int compareUsingConstraints(Component comp, RegionWithLocation a, RegionWithLocation b) throws SBOLGraphException {
        // First, check if there's a sequence constraint defining precedence
        if (comp.getConstraints()!=null) {
            for (Constraint constraint : comp.getConstraints()) {
                if (hasConstraintBetweenFeatures(constraint, a, b)) {
                    return getConstraintComparison(constraint, a, b);
                }
            }
        }
        
        // If no constraint found, fall back to location-based comparison
        return compareByEffectivePosition(a, b);
    }
    
    /**
     * Creates a FeatureWithLocation object for a feature and location.
     * @param feature the feature 
     * @param location the location of the feature
     * @return FeatureWithLocation object or null if location type not supported
     * @throws SBOLGraphException if there's an error accessing location data
     */
    /*private FeatureWithLocation createFeatureWithLocation(Feature feature, Location location) throws SBOLGraphException {
        if (location instanceof Range) {
            Range range = (Range) location;
            int start = range.getStart().get();
            int end = range.getEnd().get();
            boolean isReverse = range.getOrientation()!=null && range.getOrientation().equals(Orientation.reverseComplement);
            int effectivePosition = isReverse ? end : start;            
            return new FeatureWithLocation(feature.getDisplayId(), effectivePosition, start, end, isReverse, feature.getName(), feature.getClass().getSimpleName());
        }
        
        // Handle other location types if needed
        return null;
    }
    */

    /**
     * Checks if a sequence constraint applies to the relationship between two features.
     * @param constraint the sequence constraint to check
     * @param a the first feature
     * @param b the second feature
     * @return true if the constraint defines a relationship between these features
     */
    private boolean hasConstraintBetweenFeatures(Constraint constraint, RegionWithLocation a, RegionWithLocation b) throws SBOLGraphException{
            if (constraint.getSubject()!=null && constraint.getObject()!=null) {
                URI subjectUri = constraint.getSubject().getUri();
                URI objectUri = constraint.getObject().getUri();
                
                return (subjectUri.equals(a.featureURI) && objectUri.equals(b.featureURI)) ||
                       (subjectUri.equals(b.featureURI) && objectUri.equals(a.featureURI));
            }        
        return false;
    }
    
    /**
     * Gets the comparison result based on a sequence constraint.
     * @param constraint the sequence constraint
     * @param a the first feature
     * @param b the second feature
     * @return negative if a precedes b, positive if b precedes a, zero if no precedence
     */
    private int getConstraintComparison(Constraint constraint, RegionWithLocation a, RegionWithLocation b) throws SBOLGraphException{
            if (constraint.getRestriction() != null && constraint.getRestriction().equals(RestrictionType.SequentialRestriction.precedes.getUri())) {
                URI subjectURI = constraint.getSubject().getUri();
                URI objectURI = constraint.getObject().getUri();
                
                if (subjectURI.equals(a.featureURI) && objectURI.equals(b.featureURI)) {
                    return -1; // a precedes b
                } else if (subjectURI.equals(b.featureURI) && objectURI.equals(a.featureURI)) {
                    return 1;  // b precedes a
                }
            }
        
        return compareByEffectivePosition(a, b);
    }
    
    /**
     * Compares features by their effective positions.
     * @param a the first feature
     * @param b the second feature
     * @return comparison result based on effective positions
     */
    private int compareByEffectivePosition(RegionWithLocation a, RegionWithLocation b) {
        return Integer.compare(a.effectivePosition, b.effectivePosition);
    }
    
    /**
     * Prints the ordered components data structure.
     */
    public void printOrderedComponentsData(Map<String, List<SubComponentWithLocation>> orderedComponents) {
        System.out.println("\n=== Ordered Components by Location ===");
        
        for (Map.Entry<String, List<SubComponentWithLocation>> entry : orderedComponents.entrySet()) {
            System.out.println(entry.getKey() + ":");
            
            for (SubComponentWithLocation scLoc : entry.getValue()) {
                String displayId = scLoc.componentDisplayId + (scLoc.isReverse ? "(r)" : "");
                System.out.println("\t" + displayId + 
                    " (position: " + scLoc.startPosition + "-" + scLoc.endPosition + ")");
            }
            System.out.println();
        }
    }

    public void printOrderedRegions(Map<String, List<RegionWithLocation>> orderedComponents) throws SBOLGraphException {
        System.out.println("\n=== Ordered Regions by Location ===");
        
        for (Map.Entry<String, List<RegionWithLocation>> entry : orderedComponents.entrySet()) {
            if (entry.getValue().isEmpty()) continue; // Skip if no features
            System.out.println(entry.getKey() + ":");
            
            for (RegionWithLocation regionLoc : entry.getValue()) {
                Feature feature=regionLoc.feature;
                String displayId = regionLoc.featureDisplayId;
                if (feature instanceof SubComponent){
                    Component instanceOf = ((SubComponent) feature).getInstanceOf();
                    if (instanceOf != null) {
                        displayId = instanceOf.getDisplayId() + " --> " + displayId;
                    }
                }
                displayId +=  (regionLoc.isReverse ? "(r)" : "");
                System.out.println("\t" + displayId + 
                    " (position: " + regionLoc.startPosition + "-" + regionLoc.endPosition + ")");
            }
            System.out.println();
        }
    }


    
    /**
     * Helper class to store feature display ID with its location and orientation.
     */
    public static class RegionWithLocation {
        public final String featureDisplayId;
        public final int effectivePosition;  // Position used for sorting (start for forward, end for reverse)
        public final int startPosition;      // Actual start position
        public final int endPosition;        // Actual end position
        public final boolean isReverse;      // Whether feature is on reverse strand
        public final String featureName;     // Feature name
        public final String featureType;     // Feature type (class name)
        public final URI featureURI;     // Feature type (class name)
        public final Feature feature;     // The actual Feature instance
        
        public RegionWithLocation(String featureDisplayId, int effectivePosition, 
                               int startPosition, int endPosition, boolean isReverse,
                               String featureName, String featureType, URI featureURI, Feature feature) {
            this.featureDisplayId = featureDisplayId;
            this.effectivePosition = effectivePosition;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.isReverse = isReverse;
            this.featureName = featureName;
            this.featureType = featureType;
            this.featureURI = featureURI;
            this.feature=feature;
        }
    }
    
    /**
     * Helper class to store component display ID with its location and orientation.
     */
    public static class SubComponentWithLocation {
        public final String componentDisplayId;
        public final int effectivePosition;  // Position used for sorting (start for forward, end for reverse)
        public final int startPosition;      // Actual start position
        public final int endPosition;        // Actual end position
        public final boolean isReverse;      // Whether component is on reverse strand
        public final Component component;    // The actual Component instance referenced by instanceOf
        public final SubComponent subComponent;    // The actual SubComponent instance
        
        public SubComponentWithLocation(String componentDisplayId, int effectivePosition, 
                               int startPosition, int endPosition, boolean isReverse, SubComponent subComponent, Component component) {
            this.componentDisplayId = componentDisplayId;
            this.effectivePosition = effectivePosition;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.isReverse = isReverse;
            this.subComponent= subComponent;
            this.component = component;
        }
    }
}
