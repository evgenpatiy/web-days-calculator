package com.gmail.yevgen.spring.ui;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.MainView;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@Route("people")
@PageTitle("All users")
@StyleSheet("frontend://css/style.css")
public class AllUsersLayout extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = -7348471267394419981L;
    private final PersonRepository personRepository;

    @Autowired
    public AllUsersLayout(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void showPeopleView(UUID id) {
        H3 updateUserHeader = new H3("People");
        updateUserHeader.addClassName("pageHeader");
        Button backButton = new Button(" Back", VaadinIcon.ARROW_BACKWARD.create(), event -> {
            UI.getCurrent().navigate("account",
                    QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(id)))
                            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        });
        backButton.addClassName("topButton");
        Button exitButton = new Button(" Exit", VaadinIcon.EXIT.create(), event -> {
            UI.getCurrent().navigate(MainView.class);
        });
        exitButton.addClassName("topButton");

        HorizontalLayout topButtonsBar = new HorizontalLayout(backButton, exitButton);
        topButtonsBar.addClassName("topButtonsBar");

        HorizontalLayout topBarWithHeader = new HorizontalLayout();
        topBarWithHeader.setSizeFull();
        topBarWithHeader.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        topBarWithHeader.add(updateUserHeader);
        topBarWithHeader.add(topButtonsBar);
        add(topBarWithHeader);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        UUID id = UUID.fromString(parametersMap.get("user").get(0));
        showPeopleView(id);
    }
}
