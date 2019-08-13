package com.gmail.yevgen.spring.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@Route("dayspanel")
@PageTitle("Days calculator - user panel")
@StyleSheet("../frontend/css/style.css")
public class DaysView extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = -3227439462230694954L;
    private PersonRepository personRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DaysView(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        String personLogin = parametersMap.get("user").get(0);
        showDaysViewPanel(personLogin);
    }

    private void showDaysViewPanel(String personLogin) {
        Grid<Person> personGrid = new Grid<>(Person.class);
        personGrid.setColumns("name");
        personGrid.setItems(personRepository.findAll());

        HorizontalLayout daysViewLayout = new HorizontalLayout();
        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setResponsiveSteps(new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("600px", 1, LabelsPosition.ASIDE));

        Person person = personRepository.findByLogin(personLogin);

        Image photo = new Image();

        if (person.getProfilePicture() == null) {
            photo.setSrc("../frontend/img/anon.png");
        } else {
            StreamResource sr = new StreamResource("", () -> {
                return new ByteArrayInputStream(person.getProfilePicture());
            });
            sr.setContentType("image/png");
            photo.setSrc(sr);
        }

        TextField nameField = new TextField();
        nameField.setPlaceholder(person.getName());
        // nameField.setReadOnly(true);
        nameField.setValueChangeMode(ValueChangeMode.EAGER);

        TextField loginField = new TextField();
        loginField.setPlaceholder(person.getLogin());
        // loginField.setReadOnly(true);
        loginField.setValueChangeMode(ValueChangeMode.EAGER);

        // Locale ukrainian = new Locale("uk", "UA");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        TextField birthDateField = new TextField();
        birthDateField.setPlaceholder(person.getBirthDate().format(formatter));
        // birthDateField.setReadOnly(true);
        birthDateField.setValueChangeMode(ValueChangeMode.EAGER);

        Period period = Period.between(person.getBirthDate(), LocalDate.now());

        Label daysLivedLabel = new Label();
        daysLivedLabel.setText(period.getYears() + " years " + period.getMonths() + " months " + period.getDays()
                + " days behind your back! Keep going...");

        layoutWithBinder.addFormItem(photo, "");
        layoutWithBinder.addFormItem(nameField, "Name");
        layoutWithBinder.addFormItem(loginField, "Login");
        layoutWithBinder.addFormItem(birthDateField, "Birtdate");
        layoutWithBinder.addFormItem(daysLivedLabel, "");

        daysViewLayout.add(layoutWithBinder);
        add(daysViewLayout, personGrid);
    }
}
