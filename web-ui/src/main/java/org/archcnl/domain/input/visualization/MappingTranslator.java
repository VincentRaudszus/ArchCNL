package org.archcnl.domain.input.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.archcnl.domain.common.ConceptManager;
import org.archcnl.domain.common.RelationManager;
import org.archcnl.domain.common.VariableManager;
import org.archcnl.domain.common.conceptsandrelations.Concept;
import org.archcnl.domain.common.conceptsandrelations.CustomConcept;
import org.archcnl.domain.common.conceptsandrelations.FamixConcept;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.AndTriplets;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet.ActualObjectType;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet.Triplet;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet.Variable;
import org.archcnl.domain.input.model.mappings.ConceptMapping;
import org.archcnl.domain.input.visualization.elements.PlantUmlElement;
import org.archcnl.domain.input.visualization.exceptions.MappingToUmlTranslationFailedException;

public class MappingTranslator {

    private List<Triplet> whenTriplets;
    private ConceptManager conceptManager;
    private RelationManager relationManager;

    public MappingTranslator(
            List<Triplet> whenTriplets,
            ConceptManager conceptManager,
            RelationManager relationManager) {
        this.whenTriplets = whenTriplets;
        this.conceptManager = conceptManager;
        this.relationManager = relationManager;
    }

    public Map<Variable, PlantUmlBlock> createElementMap(Set<Variable> usedVariables)
            throws MappingToUmlTranslationFailedException {
        Set<Variable> variables = prepareMappingForTranslation();
        return createPlantUmlModels(variables, usedVariables);
    }

    public List<PlantUmlPart> translateToPlantUmlModel(Map<Variable, PlantUmlBlock> elementMap)
            throws MappingToUmlTranslationFailedException {
        TripletContainer container = new TripletContainer(whenTriplets);
        container.applyElementProperties(elementMap);
        List<PlantUmlBlock> topLevelElements = getTopLevelElements(elementMap);

        List<PlantUmlPart> parts = new ArrayList<>();
        parts.addAll(createRequiredParents(topLevelElements));
        parts.addAll(container.createConnections(elementMap));
        return parts;
    }

    private Set<Variable> prepareMappingForTranslation()
            throws MappingToUmlTranslationFailedException {
        Set<Variable> variables = inferVariableTypes();
        whenTriplets = new TripletReducer(whenTriplets, variables).reduce();
        return inferVariableTypes();
    }

    private Set<Variable> inferVariableTypes() throws MappingToUmlTranslationFailedException {
        VariableManager variableManager = new VariableManager();
        AndTriplets container = new AndTriplets(whenTriplets);
        if (variableManager.hasConflictingDynamicTypes(container, conceptManager)) {
            throw new MappingToUmlTranslationFailedException(
                    "Variable with conflicting type usage in mapping.");
        }
        return variableManager.getVariables();
    }

    private Map<Variable, PlantUmlBlock> createPlantUmlModels(
            Set<Variable> variables, Set<Variable> usedVariables)
            throws MappingToUmlTranslationFailedException {
        Map<Variable, PlantUmlBlock> elementMap = new HashMap<>();
        for (Variable variable : variables) {
            Concept elementType = selectRepresentativeElementType(variable.getDynamicTypes());
            if (elementType instanceof CustomConcept) {
                ConceptMapping mapping = tryToGetMapping((CustomConcept) elementType);
                mapping =
                        new PlantUmlTransformer(conceptManager, relationManager)
                                .flattenAndRecreate(mapping);
                ConceptVisualizer visualizer =
                        new ConceptVisualizer(
                                mapping,
                                conceptManager,
                                relationManager,
                                Optional.of(variable),
                                usedVariables);
                elementMap.put(variable, visualizer);
            } else {
                PlantUmlElement element = PlantUmlMapper.createElement(elementType, variable);
                elementMap.put(variable, element);
            }
        }
        return elementMap;
    }

    private ConceptMapping tryToGetMapping(CustomConcept concept)
            throws MappingToUmlTranslationFailedException {
        Optional<ConceptMapping> mapping = concept.getMapping();
        if (mapping.isPresent()) {
            return mapping.get();
        }
        throw new MappingToUmlTranslationFailedException(concept.getName() + " has no mapping");
    }

    private Concept selectRepresentativeElementType(Set<ActualObjectType> options)
            throws MappingToUmlTranslationFailedException {
        if (options.size() == 1) {
            return (Concept) options.iterator().next();
        }
        Concept famixClass = new FamixConcept("FamixClass", "");
        Set<ActualObjectType> customOptions =
                options.stream()
                        .filter(CustomConcept.class::isInstance)
                        .collect(Collectors.toSet());
        if (customOptions.size() == 1) {
            return (Concept) customOptions.iterator().next();
        } else if (customOptions.isEmpty() && options.contains(famixClass)) {
            // This should mostly occur with objects of the following relations:
            // imports, namespaceContains, definesNestedType, hasDeclaredType
            // FamixClass is a good default for all of them
            return famixClass;
        }
        throw new MappingToUmlTranslationFailedException(
                "No representative type could be picked from: " + options);
    }

    private List<PlantUmlBlock> getTopLevelElements(Map<Variable, PlantUmlBlock> elementMap) {
        return elementMap.values().stream()
                .filter(Predicate.not(PlantUmlBlock::hasParentBeenFound))
                .collect(Collectors.toList());
    }

    private List<PlantUmlBlock> createRequiredParents(List<PlantUmlBlock> topLevelElements) {
        return topLevelElements.stream()
                .map(PlantUmlBlock::createRequiredParentOrReturnSelf)
                .collect(Collectors.toList());
    }
}
