package org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Variable extends ObjectType {

    private String name;
    private Set<ActualObjectType> dynamicTypes;
    private boolean conflictingDynamicTypes;

    public Variable(String name) {
        this.name = name;
        this.dynamicTypes = new LinkedHashSet<>();
        this.conflictingDynamicTypes = false;
    }

    public Set<ActualObjectType> getDynamicTypes() {
        return dynamicTypes;
    }

    public void setDynamicTypes(Set<ActualObjectType> dynamicTypes) {
        this.dynamicTypes = dynamicTypes;
    }

    public void clearDynamicTypes() {
        dynamicTypes.clear();
        conflictingDynamicTypes = false;
    }

    public void refineDynamicTypes(Set<ActualObjectType> dynamicTypes) {
        if (this.dynamicTypes.isEmpty()) {
            this.dynamicTypes = dynamicTypes;
        } else {
            this.dynamicTypes.retainAll(dynamicTypes);
            if (this.dynamicTypes.isEmpty()) {
                conflictingDynamicTypes = true;
            }
        }
    }

    public boolean hasConflictingDynamicTypes() {
        return conflictingDynamicTypes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String transformToSparqlQuery() {
        return transformToAdoc();
    }

    @Override
    public String transformToGui() {
        return transformToAdoc();
    }

    @Override
    public String transformToAdoc() {
        return "?" + name;
    }

    @Override
    protected boolean requiredEqualsOverride(Object obj) {
        if (obj instanceof Variable) {
            final Variable that = (Variable) obj;
            return Objects.equals(this.getName(), that.getName());
        }
        return false;
    }

    @Override
    protected int requiredHashCodeOverride() {
        return Objects.hash(name);
    }
}
