package org.sbolstandard.converter.tests.datacuration;

import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.FeatureWithLocation;
import org.sbolstandard.core3.entity.LocalSubComponent;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.URINameSpace;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Role;
import org.apache.commons.lang3.tuple.Pair;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SBOLDesignUtil {

    public static Component extractComponent(SBOLDocument doc, Component design, int start, int end) throws SBOLGraphException
    {
        // Create a new composite component to hold the extracted region
        String id="extracted_" + start + "_" + end;
        Component extractedRegion = doc.createComponent(id, Arrays.asList(ComponentType.DNA.getUri()));
        extractedRegion.setName(id);
        extractedRegion.addRole(Role.EngineeredRegion);
        
        Sequence extractedSequence = extractSequence(doc, design, start, end);
        extractedRegion.addSequence(extractedSequence);
                
        // Find all components between start and end positions
        List<FeatureWithLocation> componentsInRange = findComponentsInRange(doc, design, start, end);
        
        // Add each component in range as a subcomponent of the extracted region
        for (FeatureWithLocation featureWithLocation : componentsInRange) {
            // Get the component instance
            FeatureWithLocation newFeatureWithLocation=null;
                
            if (featureWithLocation instanceof SubComponent) {
                SubComponent subCompInRange = (SubComponent) featureWithLocation;
                Component comp = subCompInRange.getInstanceOf();            
                // Create subcomponent in the extracted region
                newFeatureWithLocation = extractedRegion.createSubComponent(comp.getDisplayId(), comp);
            }
            else if (featureWithLocation instanceof SequenceFeature) {                
                SequenceFeature seqFeatureInRange = (SequenceFeature) featureWithLocation;
                // Create a new sequence feature in the extracted region
                newFeatureWithLocation = extractedRegion.createSequenceFeature(seqFeatureInRange.getDisplayId());
            }
            else if (featureWithLocation instanceof LocalSubComponent) {                
                LocalSubComponent localSubCompInRange = (LocalSubComponent) featureWithLocation;
                // Create local subcomponent in the extracted region
                newFeatureWithLocation = extractedRegion.createLocalSubComponent(localSubCompInRange.getDisplayId(), localSubCompInRange.getTypes());
            }

            // Copy location information, adjusting positions relative to the extracted region
            if (featureWithLocation.getLocations() != null) {
                for (Location loc : featureWithLocation.getLocations()) {
                    if (loc instanceof org.sbolstandard.core3.entity.Range) {
                        org.sbolstandard.core3.entity.Range range = (org.sbolstandard.core3.entity.Range) loc;
                        int origStart = range.getStart().get();
                        int origEnd = range.getEnd().get();

                        // Adjust positions relative to extracted region start
                        int newStart = origStart - start + 1;
                        int newEnd = origEnd - start + 1;

                        // Get the sequence from extracted region if available
                        if (extractedRegion.getSequences() != null && !extractedRegion.getSequences().isEmpty()) {
                            org.sbolstandard.core3.entity.Range newRange = newFeatureWithLocation.createRange(
                                    newStart, newEnd, extractedRegion.getSequences().get(0));
                            if (range.getOrientation() != null) {
                                newRange.setOrientation(range.getOrientation());
                            }
                        }
                    }
                }
            }
            // Remove the original subcomponent from design
            featureWithLocation.remove();
        }
                
        // Add the extracted region as a subcomponent to the design
        SubComponent extractedSubComp = design.createSubComponent("sc_extracted_" + start + "_" + end, extractedRegion);
        
        // Add location for the extracted subcomponent
        if (design.getSequences() != null && !design.getSequences().isEmpty()) {
            extractedSubComp.createRange(start, end, design.getSequences().get(0));
        }
        
        return extractedRegion;
    }

    private static Sequence extractSequence(SBOLDocument doc, Component design, int start, int end) throws SBOLGraphException {
        // Extract and assign the sequence for the new composite component first
        Sequence sequence = null;
        if (design.getSequences() != null && !design.getSequences().isEmpty()) {
            org.sbolstandard.core3.entity.Sequence designSeq = design.getSequences().get(0);
            String designElements = designSeq.getElements();
            
            if (designElements != null && start >= 1 && end <= designElements.length()) {
                // Extract substring (convert from 1-based to 0-based indexing)
                String extractedSeq = designElements.substring(start - 1, end);
                
                // Create sequence for extracted region
                org.sbolstandard.core3.entity.Sequence extractedSequence = doc.createSequence("extracted_" + start + "_" + end + "_sequence");
                extractedSequence.setElements(extractedSeq);
                extractedSequence.setEncoding(designSeq.getEncoding());
                sequence = extractedSequence;
            }
        }
        return sequence;
    }

    public static Component extractComponent(SBOLDocument doc, Component design, Component startComp, Component endComponent) throws SBOLGraphException
    {
        // Create a new composite component to hold the extracted region
        Component extractedRegion = doc.createComponent(startComp.getDisplayId() + "_" + endComponent.getDisplayId(), Arrays.asList(ComponentType.DNA.getUri()));
        
        // Find all components between start and end components
        List<GenBankComponentView.SubComponentWithLocation> componentsInRange = findComponentsBetween(doc, design, startComp, endComponent);
        
        // Add each component in range as a subcomponent of the extracted region
        for (GenBankComponentView.SubComponentWithLocation subCompWithLocation : componentsInRange) {
            SubComponent subComp = extractedRegion.createSubComponent(subCompWithLocation.component.getDisplayId(), subCompWithLocation.component);
            subCompWithLocation.subComponent.remove();
        }
        
        return extractedRegion;
    }
    
    /**
     * Find all components between the start and end components using GenBankComponentView's getOrderedComponents
     * @throws SBOLGraphException 
     */
    private static List<GenBankComponentView.SubComponentWithLocation> findComponentsBetween(SBOLDocument doc, Component design, Component startComp, Component endComp) throws SBOLGraphException {
        List<GenBankComponentView.SubComponentWithLocation> result = new ArrayList<>();
        
        
        // Use GenBankComponentView's getOrderedComponents method
        GenBankComponentView view = new GenBankComponentView();
        Map<String, List<GenBankComponentView.SubComponentWithLocation>> orderedComponents = view.getOrderedComponents(doc);
        
        // Get the ordered components for the design
        List<GenBankComponentView.SubComponentWithLocation> designComponents = orderedComponents.get(design.getDisplayId());
        if (designComponents == null || designComponents.isEmpty()) {
            return result;
        }
        
        // Find indices of start and end components
        int startIndex = -1;
        int endIndex = -1;
        
        for (int i = 0; i < designComponents.size(); i++) {
            Component comp = designComponents.get(i).component;
            if (comp.getUri().equals(startComp.getUri())) {
                startIndex = i;
            }
            if (comp.getUri().equals(endComp.getUri())) {
                endIndex = i;
            }
        }
        
        // Ensure proper ordering
        if (startIndex > endIndex) {
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }
        
        // Add all components in the range (inclusive)
        if (startIndex >= 0 && endIndex >= 0) {
            for (int i = startIndex; i <= endIndex; i++) {
                result.add(designComponents.get(i));
            }
        }
        
        return result;
    }
    
    /**
     * Find all subcomponents within a specific sequence position range
     * @throws SBOLGraphException 
     */
    private static List<FeatureWithLocation> findComponentsInRange(SBOLDocument doc, Component design, int start, int end) throws SBOLGraphException {
        List<FeatureWithLocation> result = new ArrayList<>();
        
        // Get all subcomponents from the design
        List<Feature> subComponents = design.getFeatures();
        if (subComponents == null || subComponents.isEmpty()) {
            return result;
        }
        
        // Filter subcomponents that fall within the start-end range
        for (Feature subComp : subComponents) {
            if (subComp instanceof FeatureWithLocation) {
                FeatureWithLocation featureWithLocation = (FeatureWithLocation) subComp;

                if (featureWithLocation.getLocations() != null) {
                    Pair<Integer, Integer> range = Location.getStartEnd(featureWithLocation.getLocations());
                    if (range != null) {
                        Pair<Integer, Integer> normalizedRange = getNormalizedRange(range);
                        int subStart = normalizedRange.getLeft();
                        int subEnd = normalizedRange.getRight();
                        
                        // Check if this component's range is contained within start-end
                        boolean isInRange = (subStart >= start && subEnd <= end);
                        
                        if (isInRange) {
                            result.add(featureWithLocation);
                        }
                    }
                }
            }
        }        
        return result;
    }
    
    /**
     * Normalizes a range pair to ensure start <= end
     * @param range the range pair (possibly reversed)
     * @return normalized pair with minimum as left and maximum as right
     */
    private static Pair<Integer, Integer> getNormalizedRange(Pair<Integer, Integer> range) {
        int start = Math.min(range.getLeft(), range.getRight());
        int end = Math.max(range.getLeft(), range.getRight());
        return Pair.of(start, end);
    }

    public static String getReverseComplement(String sequence) {
        StringBuilder revComp = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            char base = sequence.charAt(i);
            switch (base) {
                case 'A':
                    revComp.append('T');
                    break;
                case 'T':
                    revComp.append('A');
                    break;
                case 'C':
                    revComp.append('G');
                    break;
                case 'G':
                    revComp.append('C');
                    break;
                case 'a':
                    revComp.append('t');
                    break;
                case 't':
                    revComp.append('a');
                    break;
                case 'c':
                    revComp.append('g');
                    break;
                case 'g':
                    revComp.append('c');
                    break;
                default:
                    revComp.append(base); // For non-ATCG characters, just append as is
            }
        }
        return revComp.toString();
}
}