package com.gmail.yevgen.spring.ui;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.repository.PersonRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
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
public final class AllUsersView extends VerticalLayout implements HasUrlParameter<String> {
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

        Grid<Person> grid = new Grid<>(Person.class);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS,
                GridVariant.LUMO_ROW_STRIPES);
        grid.setColumns("name");
        grid.addColumn(
                new LocalDateRenderer<>(Person::getBirthDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Birth Date").setSortable(true).setComparator((person1, person2) -> {
                    return person1.getBirthDate().compareTo(person2.getBirthDate());
                });
        grid.addColumn(person -> Long.toString(ChronoUnit.DAYS.between(person.getBirthDate(), LocalDate.now())))
                .setHeader("Days lived").setSortable(true).setComparator((person1, person2) -> {
                    return person1.getBirthDate().compareTo(person2.getBirthDate());
                });

        grid.setItems(personRepository.findAll());
        grid.addItemClickListener(event -> showDaysViewPanel(event.getItem().getId()));

        Label searchLabel = new Label("Search: ");
        TextField nameSearchField = new TextField();
        nameSearchField.setPlaceholder("by name");
        nameSearchField.setClearButtonVisible(true);
        nameSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        nameSearchField.addValueChangeListener(event -> grid
                .setItems(personRepository.findByAttributeContainsText("name", nameSearchField.getValue())));

        DatePicker dateSearchField = new DatePicker();
        dateSearchField.setPlaceholder("born before");
        dateSearchField.addValueChangeListener(
                event -> grid.setItems(personRepository.findByDateBefore(dateSearchField.getValue())));

        HorizontalLayout searchBar = new HorizontalLayout(searchLabel, nameSearchField, dateSearchField);
        searchBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        searchBar.getChildren().forEach(component -> component.setId("whiteText"));
        add(searchBar, grid);
    }

    private final void showDaysViewPanel(UUID id) {
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
        periodDetails.setSummaryText("see what behind the back");
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
