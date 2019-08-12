package com.gmail.yevgen.spring.ui;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@Route("dayspanel")
@PageTitle("Days calculator - user panel")
@StyleSheet("../frontend/css/style.css")
public class DaysView extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = -3227439462230694954L;
    private PersonRepository personRepository;

    @Autowired
    public DaysView(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        String passedUser = parametersMap.get("user").get(0);
        add(new Text("Person: " + personRepository.findByLogin(passedUser)));
    }
}
