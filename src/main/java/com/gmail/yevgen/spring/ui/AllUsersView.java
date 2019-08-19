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

import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.Item;
import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@Route(value = AllUsersView.ROUTE, layout = MainLayout.class)
@PageTitle("All users")
public class AllUsersView extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = -7348471267394419981L;
    public static final String ROUTE = "people";
    private final PersonRepository personRepository;

    @Autowired
    public AllUsersView(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void showPeopleView(UUID id) {
        Label updateUserHeader = new Label("People");
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

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.setOrientation(Orientation.HORIZONTAL);
        splitLayout.addToSecondary(new Label("Charts"));

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setHeight("500px");
        leftLayout.addClassName("scrollable");
        personRepository.findAll().forEach(person -> {
            RippleClickableCard card = new RippleClickableCard(onClick -> {
                showDaysViewPanel(person.getId());
            }, new Item(person.getName(), ChronoUnit.DAYS.between(person.getBirthDate(), LocalDate.now()) + " days"));
            card.setWidth("200px");
            leftLayout.add(card);
        });

        splitLayout.addToPrimary(leftLayout);
        add(splitLayout);
    }

    private void showDaysViewPanel(UUID id) {
        Person person = personRepository.findById(id).get();
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);

        Label nameLabel = new Label(person.getName());
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

        Label totalDaysLabel = new Label(
                "Total days: " + ChronoUnit.DAYS.between(person.getBirthDate(), LocalDate.now()));
        totalDaysLabel.addClassName("timesDiv");

        Period period = Period.between(person.getBirthDate(), LocalDate.now());
        Details periodDetails = new Details();
        periodDetails.setSummaryText("see what behind my back");
        Label yearMonthDayLabel = new Label(
                period.getYears() + " years, " + period.getMonths() + " months, " + period.getDays() + " days");
        VerticalLayout detailsLayout = new VerticalLayout(yearMonthDayLabel, totalDaysLabel);

        periodDetails.addContent(detailsLayout);
        mainLayout.add(nameLabel, photo, birthDateLabel, periodDetails);

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.add(mainLayout);
        dialog.setOpened(true);
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
