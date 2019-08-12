package com.gmail.yevgen.spring.ui;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.MainView;
import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@Route("signup")
@PageTitle("Days calculator - new user")
public class SignUpView extends VerticalLayout {
    private static final long serialVersionUID = 2659811876997659447L;
    private final PersonRepository personRepository;
    private Person person;

    @Autowired
    public SignUpView(PersonRepository personRepository) {
        this.personRepository = personRepository;
        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setResponsiveSteps(new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("600px", 1, LabelsPosition.ASIDE));

        Binder<Person> binder = new Binder<>();
        person = new Person();

        Image photo = new Image();
        photo.setSrc("../frontend/img/anon.png");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Span dropLabel = new Span("drag photo here");
        upload.setUploadButton(new Button("Upload"));
        upload.setDropLabel(dropLabel);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            StreamResource sr = new StreamResource("", () -> {
                return buffer.getInputStream();
            });
            if (sr != null) {
                sr.setContentType("image/png");
                photo.setSrc(sr);
                photo.setWidth("128px");
                photo.setHeight("128px");
            }
        });

        TextField name = new TextField();
        name.setValueChangeMode(ValueChangeMode.EAGER);

        TextField loginName = new TextField();
        loginName.setValueChangeMode(ValueChangeMode.EAGER);

        PasswordField password = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.EAGER);
        PasswordField confirmPassword = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.EAGER);

        DatePicker birthDate = new DatePicker();

        Button confirmButton = new Button("Confirm", VaadinIcon.USER.create());
        Button resetButton = new Button("Reset", VaadinIcon.WARNING.create());
        Button cancelButton = new Button("Cancel", VaadinIcon.ARROW_BACKWARD.create());

        layoutWithBinder.addFormItem(photo, "");
        layoutWithBinder.addFormItem(upload, "Photo");
        layoutWithBinder.addFormItem(name, "Name");
        layoutWithBinder.addFormItem(loginName, "Login");
        layoutWithBinder.addFormItem(password, "Password");
        layoutWithBinder.addFormItem(confirmPassword, "Conform password");
        layoutWithBinder.addFormItem(birthDate, "Birthdate");

        HorizontalLayout buttonsLine = new HorizontalLayout();
        buttonsLine.add(confirmButton, resetButton, cancelButton);
        confirmButton.getStyle().set("marginRight", "10px");
        resetButton.getStyle().set("marginRight", "10px");

        name.setRequiredIndicatorVisible(true);
        loginName.setRequiredIndicatorVisible(true);
        password.setRequiredIndicatorVisible(true);
        confirmPassword.setRequiredIndicatorVisible(true);
        birthDate.setRequiredIndicatorVisible(true);

        // user input validation
        binder.forField(name).withValidator(new StringLengthValidator("Name is mandatory", 1, null))
                .bind(Person::getName, Person::setName);

        binder.forField(loginName).withValidator(new StringLengthValidator("Login is mandatory", 1, null))
                .bind(Person::getLogin, Person::setLogin);

        binder.forField(password).withValidator(new StringLengthValidator("Password is mandatory", 1, null))
                .bind(Person::getPassword, Person::setPassword);
        binder.forField(confirmPassword)
                .withValidator(new StringLengthValidator("Password confirmation is mandatory", 1, null))
                .withValidator(confirm -> confirm.equals(password.getValue()),
                        "Password doesn't match its confirmation")
                .bind(Person::getPassword, Person::setPassword);

        Binder.Binding<Person, String> confirmationBinding = binder.forField(confirmPassword)
                .withValidator(confirm -> confirm.equals(password.getValue()),
                        "Password doesn't match its confirmation")
                .bind(Person::getPassword, Person::setPassword);
        password.addValueChangeListener(event -> confirmationBinding.validate());

        binder.forField(birthDate).withValidator(bd -> bd != null, "Birthdate is mandatory")
                .withValidator(
                        new DateRangeValidator("Birthdate out of sense", LocalDate.ofYearDay(1, 1), LocalDate.now()))
                .bind(Person::getBirthDate, Person::setBirthDate);

        confirmButton.addClickListener(event -> {
            if (binder.writeBeanIfValid(person)) {
                if (ifPersonWithLoginExists(person)) {
                    Notification.show("User " + person.getLogin() + " already exists, choose different username please",
                            3000, Position.MIDDLE);
                } else {
                    savePerson(person);
                    UI.getCurrent().navigate("dayspanel",
                            QueryParameters.simple(Stream.of(new SimpleEntry<>("user", person.getLogin()))
                                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
                }
            } else {
                BinderValidationStatus<Person> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                Notification.show("There are errors: " + errorText);
            }
        });
        resetButton.addClickListener(event -> {
            binder.readBean(null);
        });
        cancelButton.addClickListener(event -> UI.getCurrent().navigate(MainView.class));

        add(layoutWithBinder, buttonsLine);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    void savePerson(Person p) {
        personRepository.save(p);
    }

    boolean ifPersonWithLoginExists(Person p) {
        return personRepository.findByLogin(p.getLogin()) != null;
    }
}
