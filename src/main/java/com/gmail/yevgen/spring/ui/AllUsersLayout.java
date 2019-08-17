package com.gmail.yevgen.spring.ui;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H3;
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
    private final PersonRepository personRepository;

    @Autowired
    public AllUsersLayout(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void showPeopleView(UUID id) {
        H3 updateUserHeader = new H3("People around me");
        updateUserHeader.addClassName("pageHeader");
        add(updateUserHeader);
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
