package com.gmail.yevgen.spring.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.MainView;
import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
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
import com.vaadin.flow.server.StreamResource;

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
        String personLogin = parametersMap.get("user").get(0);
        showDaysViewPanel(personLogin);
    }

    private void showDaysViewPanel(String personLogin) {
        Person person = personRepository.findByLogin(personLogin);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);

        Period period = Period.between(person.getBirthDate(), LocalDate.now());

        H2 nameLabel = new H2(person.getName());
        nameLabel.addClassName("nameLabel");

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String birthDateText = "was born at " + person.getBirthDate().format(formatter);
        Label birthDateLabel = new Label(birthDateText);
        birthDateLabel.addClassName("line");

        H3 totalDays = new H3("Total days: " + ChronoUnit.DAYS.between(person.getBirthDate(), LocalDate.now()));
        totalDays.addClassName("timesDiv");
        Div yearsDiv = new Div(new Span(period.getYears() + " years"));
        Div monthsDiv = new Div(new Text(period.getMonths() + " months"));
        Div daysDiv = new Div(new Text(period.getDays() + " days"));

        Details periodDetails = new Details();
        periodDetails.setSummaryText("Behind my back:");
        periodDetails.addContent(yearsDiv, monthsDiv, daysDiv, totalDays);

        mainLayout.add(nameLabel, photo, birthDateLabel, periodDetails);

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button editButton = new Button(" Edit", VaadinIcon.EDIT.create());

        Button deleteButton = new Button(" Delete", VaadinIcon.CLOSE_CIRCLE.create());

        Button logoutButton = new Button(" Logout", VaadinIcon.SIGN_OUT.create(), e -> {
            UI.getCurrent().navigate(MainView.class);
            dialog.close();
        });
        HorizontalLayout dialogButtonsBar = new HorizontalLayout(editButton, deleteButton, logoutButton);
        dialog.add(mainLayout, dialogButtonsBar);
        dialog.setOpened(true);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
