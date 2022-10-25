package org.archcnl.domain.input.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.archcnl.domain.common.ConceptManager;
import org.archcnl.domain.common.RelationManager;
import org.archcnl.domain.common.conceptsandrelations.CustomConcept;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.AndTriplets;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet.Triplet;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet.Variable;
import org.archcnl.domain.input.model.mappings.ConceptMapping;
import org.archcnl.domain.input.visualization.connections.BasicConnection;
import org.archcnl.domain.input.visualization.elements.CustomConceptElement;
import org.archcnl.domain.input.visualization.elements.PlantUmlElement;
import org.archcnl.domain.input.visualization.exceptions.MappingToUmlTranslationFailedException;
import org.archcnl.domain.input.visualization.exceptions.PropertyNotFoundException;
import org.archcnl.domain.input.visualization.helpers.NamePicker;

public class ConceptVisualizer extends MappingVisualizer implements PlantUmlBlock {

    private CustomConceptElement conceptElement;
    private List<ConceptVariant> variants = new ArrayList<>();

    public ConceptVisualizer(
            ConceptMapping mapping,
            ConceptManager conceptManager,
            RelationManager relationManager,
            Optional<Variable> parentSubject,
            Set<Variable> usedVariables)
            throws MappingToUmlTranslationFailedException {
        super(mapping, conceptManager, relationManager, usedVariables);
        createConceptElement();
        createVariants(parentSubject);
    }

    private void createConceptElement() {
        CustomConcept concept = (CustomConcept) mapping.getThenTriplet().getObject();

        Variable uniqueVar =
                NamePicker.pickUniqueVariable(
                        usedVariables, new HashMap<>(), new Variable(concept.getName()));

        this.conceptElement = new CustomConceptElement(concept, uniqueVar.getName());
    }

    private void createVariants(Optional<Variable> parentSubject)
            throws MappingToUmlTranslationFailedException {
        List<AndTriplets> whenTriplets = mapping.getWhenTriplets();
        Triplet thenTriplet = mapping.getThenTriplet();
        throwWhenNoVariants(whenTriplets);

        for (int i = 0; i < whenTriplets.size(); i++) {
            AndTriplets whenVariant = whenTriplets.get(i);
            String variantName = mappingName + (i + 1);
            variants.add(
                    new ConceptVariant(
                            whenVariant,
                            thenTriplet,
                            variantName,
                            conceptManager,
                            relationManager,
                            parentSubject,
                            usedVariables));
        }
    }

    @Override
    public String buildPlantUmlCode() {
        boolean printBorder = moreThanOneVariant();
        StringBuilder builder = new StringBuilder();
        for (ConceptVariant variant : variants) {
            builder.append(variant.buildPlantUmlCode(printBorder));
            builder.append("\n");
        }
        builder.append(buildConceptSection());
        return builder.toString();
    }

    private String buildConceptSection() {
        StringBuilder builder = new StringBuilder();
        builder.append(conceptElement.buildPlantUmlCode());
        for (ConceptVariant variant : variants) {
            List<String> conceptIds = conceptElement.getIdentifiers();
            List<String> objectIds = variant.getIdentifiers();
            for (String conceptId : conceptIds) {
                for (String objectId : objectIds) {
                    BasicConnection connection = new BasicConnection(conceptId, objectId);
                    builder.append("\n");
                    builder.append(connection.buildPlantUmlCode());
                }
            }
        }
        return builder.toString();
    }

    @Override
    public void setProperty(String property, Object object) throws PropertyNotFoundException {
        for (var variant : variants) {
            variant.setProperty(property, object);
        }
    }

    @Override
    public boolean hasParentBeenFound() {
        boolean result = true;
        for (PlantUmlElement element : getBaseElements()) {
            result = result && element.hasParentBeenFound();
        }
        return result;
    }

    @Override
    public List<String> getIdentifiers() {
        return variants.stream()
                .map(ConceptVariant::getIdentifiers)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<PlantUmlElement> getBaseElements() {
        return variants.stream()
                .map(ConceptVariant::getBaseElements)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean moreThanOneVariant() {
        return variants.size() > 1;
    }

    @Override
    public PlantUmlBlock createRequiredParentOrReturnSelf() {
        for (var variant : variants) {
            variant.getBaseElements().forEach(PlantUmlBlock::createRequiredParentOrReturnSelf);
        }
        return this;
    }
}
