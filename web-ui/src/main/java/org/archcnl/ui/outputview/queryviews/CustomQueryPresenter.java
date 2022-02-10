package org.archcnl.ui.outputview.queryviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.shared.Registration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.archcnl.domain.common.VariableManager;
import org.archcnl.domain.common.conceptsandrelations.andtriplets.triplet.Variable;
import org.archcnl.domain.input.exceptions.InvalidVariableNameException;
import org.archcnl.domain.input.exceptions.VariableAlreadyExistsException;
import org.archcnl.domain.output.model.query.Query;
import org.archcnl.domain.output.model.query.SelectClause;
import org.archcnl.domain.output.model.query.WhereClause;
import org.archcnl.ui.common.andtriplets.AndTripletsEditorPresenter;
import org.archcnl.ui.common.andtriplets.triplet.VariableSelectionComponent;
import org.archcnl.ui.common.andtriplets.triplet.events.ConceptListUpdateRequestedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.ConceptSelectedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.PredicateSelectedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.RelationListUpdateRequestedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.VariableCreationRequestedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.VariableFilterChangedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.VariableListUpdateRequestedEvent;
import org.archcnl.ui.common.andtriplets.triplet.events.VariableSelectedEvent;
import org.archcnl.ui.common.andtriplets.triplet.exceptions.SubjectOrObjectNotDefinedException;
import org.archcnl.ui.events.ConceptGridUpdateRequestedEvent;
import org.archcnl.ui.events.ConceptHierarchySwapRequestedEvent;
import org.archcnl.ui.events.RelationGridUpdateRequestedEvent;
import org.archcnl.ui.events.RelationHierarchySwapRequestedEvent;
import org.archcnl.ui.outputview.queryviews.events.PinCustomQueryRequestedEvent;
import org.archcnl.ui.outputview.queryviews.events.PinQueryButtonPressedEvent;
import org.archcnl.ui.outputview.queryviews.events.RunButtonPressedEvent;
import org.archcnl.ui.outputview.queryviews.events.RunQueryRequestedEvent;
import org.archcnl.ui.outputview.queryviews.events.UpdateQueryTextButtonPressedEvent;

@Tag("CustomQueryPresenter")
public class CustomQueryPresenter extends Component {

    private static final long serialVersionUID = -2223963205535144587L;
    private static final String DEFAULT_NAME = "Pinned Custom Query";
    private CustomQueryView view;
    private AndTripletsEditorPresenter wherePresenter;
    private VariableManager variableManager;
    private String queryName;

    public CustomQueryPresenter() {
        queryName = DEFAULT_NAME;
        wherePresenter = new AndTripletsEditorPresenter(false);
        view = new CustomQueryView(wherePresenter.getAndTripletsEditorView());
        variableManager = new VariableManager();

        addListeners();
    }

    private void addListeners() {
        view.addListener(ConceptGridUpdateRequestedEvent.class, this::fireEvent);
        view.addListener(RelationGridUpdateRequestedEvent.class, this::fireEvent);

        view.addListener(ConceptHierarchySwapRequestedEvent.class, this::fireEvent);
        view.addListener(RelationHierarchySwapRequestedEvent.class, this::fireEvent);

        view.addListener(
                VariableFilterChangedEvent.class, event -> event.handleEvent(variableManager));
        view.addListener(VariableCreationRequestedEvent.class, this::addVariable);
        view.addListener(
                VariableListUpdateRequestedEvent.class,
                event -> event.handleEvent(variableManager));
        view.addListener(VariableSelectedEvent.class, this::handleEvent);
        view.addListener(
                RunButtonPressedEvent.class,
                e ->
                        fireEvent(
                                new RunQueryRequestedEvent(
                                        this, true, makeQuery(), view.getGridView())));
        view.addListener(UpdateQueryTextButtonPressedEvent.class, this::handleEvent);
        view.addListener(
                PinQueryButtonPressedEvent.class,
                e -> {
                    queryName = view.getQueryName().orElse(DEFAULT_NAME);
                    view.setQueryName(queryName);
                    // TODO: Allow pinning after implementing cloning
                    view.setPinButtonVisible(false);
                    fireEvent(new PinCustomQueryRequestedEvent(this, true, getView(), queryName));
                });

        wherePresenter.addListener(
                VariableFilterChangedEvent.class, event -> event.handleEvent(variableManager));
        wherePresenter.addListener(VariableCreationRequestedEvent.class, this::addVariable);
        wherePresenter.addListener(
                VariableListUpdateRequestedEvent.class,
                event -> event.handleEvent(variableManager));
        wherePresenter.addListener(PredicateSelectedEvent.class, this::fireEvent);
        wherePresenter.addListener(RelationListUpdateRequestedEvent.class, this::fireEvent);
        wherePresenter.addListener(ConceptListUpdateRequestedEvent.class, this::fireEvent);
        wherePresenter.addListener(ConceptSelectedEvent.class, this::fireEvent);
    }

    private void addVariable(VariableCreationRequestedEvent event) {
        try {
            Variable newVariable = new Variable(event.getVariableName());
            try {
                variableManager.addVariable(newVariable);
            } catch (VariableAlreadyExistsException e) {
                // do nothing
            }
            event.getSource()
                    .setItems(variableManager.getVariables().stream().map(Variable::getName));
            event.getSource().setValue(newVariable.getName());
            view.getVariableListView().showVariableList(variableManager.getVariables());
        } catch (InvalidVariableNameException e1) {
            event.getSource().showErrorMessage("Invalid variable name");
        }
    }

    private void handleEvent(VariableSelectedEvent event) {
        if (!view.isAnyVariableSelectionComponentEmpty()) {
            view.addVariableSelectionComponent();
        } else if (event.getSource().getOptionalValue().isEmpty()
                && view.areAtleastTwoVariableSelectionComponentsEmpty()) {
            // TODO fix this behavior (see ArchCNL-154)
            // view.removeVariableSelectionComponent(event.getSource());
        }
    }

    private void handleEvent(UpdateQueryTextButtonPressedEvent event) {
        view.setQueryTextArea(getQuery());
    }

    public Query makeQuery() {
        SelectClause selectClause =
                new SelectClause(
                        view.getSelectComponents().stream()
                                .map(this::getOptionalVariable)
                                .flatMap(Optional::stream)
                                .collect(Collectors.toSet()));
        WhereClause whereClause = new WhereClause(wherePresenter.getAndTriplets());
        return new Query(queryName, selectClause, whereClause);
    }

    public void setQueryName(String name) {
        queryName = name;
        view.setQueryName(name);
    }

    public void setWhereClause(WhereClause clause) {
        wherePresenter.showAndTriplets(clause.getAndTriplets());
    }

    public void setSelectClause(SelectClause clause) {
        List<VariableSelectionComponent> components = new LinkedList<>();
        clause.getObjects()
                .forEach(
                        variable -> {
                            try {
                                variableManager.addVariable(variable);
                            } catch (VariableAlreadyExistsException e) {
                                // do nothing
                            }
                            VariableSelectionComponent component = new VariableSelectionComponent();
                            component.setItems(
                                    variableManager.getVariables().stream().map(Variable::getName));
                            component.setVariable(variable);
                            components.add(component);
                        });
        view.showVariableSelectionComponents(components);
        view.getVariableListView().showVariableList(variableManager.getVariables());
    }

    public CustomQueryView getView() {
        return view;
    }

    public String getQuery() {
        return makeQuery().transformToGui();
    }

    private Optional<Variable> getOptionalVariable(
            VariableSelectionComponent variableSelectionComponent) {
        try {
            return Optional.of(variableSelectionComponent.getVariable());
        } catch (SubjectOrObjectNotDefinedException | InvalidVariableNameException e) {
            return Optional.empty();
        }
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(
            final Class<T> eventType, final ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
