package org.archcnl.ui.input;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import org.archcnl.domain.input.model.RulesConceptsAndRelations;
import org.archcnl.domain.input.model.mappings.Concept;
import org.archcnl.domain.input.model.mappings.Relation;
import org.archcnl.ui.output.component.QueryView;

public class ConceptAndRelationView extends VerticalLayout implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;

    CreateNewLayout createNewConceptLayout = new CreateNewLayout("Concepts", "Create new concept");
    CreateNewLayout createNewRelationLayout =
            new CreateNewLayout("Relations", "Create new relation");
    MappingListLayout conceptTreeGrid;
    MappingListLayout relationTreeGrid;

    public ConceptAndRelationView() {
        RulesConceptsAndRelations.getInstance().getConceptManager().addPropertyChangeListener(this);
        RulesConceptsAndRelations.getInstance()
                .getRelationManager()
                .addPropertyChangeListener(this);
        createNewConceptLayout.setHeight(46, Unit.PERCENTAGE);
        createNewRelationLayout.setHeight(46, Unit.PERCENTAGE);
        updateConceptView();
        updateRelationView();

        add(createNewConceptLayout);
        add(createNewRelationLayout);
        add(
                new Button(
                        "Check for violations",
                        click -> getUI().ifPresent(ui -> ui.navigate(QueryView.class))));
        getStyle().set("border", "1px solid black");
    }

    private void updateConceptView() {
        List<Concept> concepts =
                RulesConceptsAndRelations.getInstance().getConceptManager().getConcepts();
        List<MappingListEntry> conceptData = new LinkedList<>();
        ConceptListEntry defaultConceptsStub = new ConceptListEntry("Default Concepts", concepts);
        conceptData.add(defaultConceptsStub);
        if (conceptTreeGrid != null) {
            createNewConceptLayout.remove(conceptTreeGrid);
        }
        conceptTreeGrid = new MappingListLayout(conceptData);
        createNewConceptLayout.add(conceptTreeGrid);
    }

    private void updateRelationView() {
        List<Relation> relations =
                RulesConceptsAndRelations.getInstance().getRelationManager().getRelations();
        List<MappingListEntry> relationData = new LinkedList<>();
        RelationListEntry defaultRelationsStub =
                new RelationListEntry("Default Relations", relations);
        relationData.add(defaultRelationsStub);
        if (relationTreeGrid != null) {
            createNewRelationLayout.remove(relationTreeGrid);
        }
        relationTreeGrid = new MappingListLayout(relationData);
        createNewRelationLayout.add(relationTreeGrid);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateConceptView();
        updateRelationView();
    }
}
