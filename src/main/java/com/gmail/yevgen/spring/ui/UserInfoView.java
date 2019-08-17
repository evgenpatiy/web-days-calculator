package com.gmail.yevgen.spring.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.MainView;
import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
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
import com.vaadin.flow.component.notification.Notification;
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

@Route("account")
@PageTitle("View account info")
@StyleSheet("frontend://css/style.css")
public class UserInfoView extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = -3227439462230694954L;
    private PersonRepository personRepository;

    @Autowired
    public UserInfoView(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        UUID id = UUID.fromString(parametersMap.get("user").get(0));
        showDaysViewPanel(id);
    }

    private void showDaysViewPanel(UUID id) {
        Person person = personRepository.findById(id).get();
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);

        H3 viewDetailsHeader = new H3("View account info");
        viewDetailsHeader.addClassName("pageHeader");
        add(viewDetailsHeader);

        H2 nameLabel = new H2(person.getName());
        nameLabel.addClassName("nameLabel");

        Image photo = new Image();

        if (person.getProfilePicture() == null) {
            photo.setSrc("frontend/img/anon.png");
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
        Period period = Period.between(person.getBirthDate(), LocalDate.now());
        Div yearsDiv = new Div(new Span('\uA78F' + " " + period.getYears() + " years"));
        Div monthsDiv = new Div(new Span('\uA78F' + " " + period.getMonths() + " months"));
        Div daysDiv = new Div(new Span('\uA78F' + " " + period.getDays() + " days"));

        Details periodDetails = new Details();
        periodDetails.setSummaryText("see what behind my back");
        periodDetails.addContent(yearsDiv, monthsDiv, daysDiv, totalDays);

        mainLayout.add(nameLabel, photo, birthDateLabel, periodDetails);

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button editButton = new Button("Edit", e -> {
            dialog.close();
            UI.getCurrent().navigate("update",
                    QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(person.getId())))
                            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        });

        Button deleteButton = new Button("Delete", e -> {
            Dialog deleteAccountDialog = new Dialog();
            deleteAccountDialog.setCloseOnEsc(true);
            deleteAccountDialog.setCloseOnOutsideClick(true);

            Button confirmDeleteButton = new Button("Delete", evt -> {
                deleteAccountDialog.close();
                dialog.close();
                personRepository.delete(person);
                Notification.show("Account of " + person.getName() + " deleted");
                UI.getCurrent().navigate(MainView.class);
            });
            Button cancelDeleteButton = new Button("Let me stay", evt -> {
                deleteAccountDialog.close();
            });

            HorizontalLayout deleteDialogButtons = new HorizontalLayout(confirmDeleteButton, cancelDeleteButton);
            H3 attentionDeleteMessage = new H3("Attention! You're about to delete your account!");
            attentionDeleteMessage.addClassName("errorNotification");
            VerticalLayout deleteDialogLayout = new VerticalLayout(attentionDeleteMessage, deleteDialogButtons);
            deleteDialogLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            deleteDialogLayout.setAlignItems(Alignment.CENTER);

            deleteAccountDialog.add(deleteDialogLayout);
            deleteAccountDialog.setOpened(true);
        });

        Button logoutButton = new Button("Exit", e -> {
            dialog.close();
            UI.getCurrent().navigate(MainView.class);
        });

        Button seeOtherPeopleButton = new Button("See people", e -> {
            dialog.close();
            UI.getCurrent().navigate("people",
                    QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(person.getId())))
                            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        });
        HorizontalLayout seeOtherPeopleBar = new HorizontalLayout(seeOtherPeopleButton);
        seeOtherPeopleBar.setJustifyContentMode(JustifyContentMode.CENTER);
        HorizontalLayout dialogButtonsBar = new HorizontalLayout(editButton, deleteButton, logoutButton);
        dialog.add(mainLayout, seeOtherPeopleBar, dialogButtonsBar);
        dialog.setOpened(true);
    }
}
