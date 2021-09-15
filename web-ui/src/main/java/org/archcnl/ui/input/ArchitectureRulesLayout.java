package org.archcnl.ui.input;

import org.archcnl.ui.input.mappingeditor.RulesOrMappingCreationLayout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ArchitectureRulesLayout extends RulesOrMappingCreationLayout {

    private static final long serialVersionUID = 1L;

    CreateNewLayout createNewRuleLayout;
    VerticalLayout rulesLayout = new VerticalLayout();

    public ArchitectureRulesLayout() {
        ButtonClickResponder ruleCreationClickResponder = () -> {};
        createNewRuleLayout =
                new CreateNewLayout(
                        "Architecture Rules", "Create new Arch Rule", ruleCreationClickResponder);
        add(createNewRuleLayout);
        add(rulesLayout);
        getStyle().set("border", "1px solid black");
    }
}
