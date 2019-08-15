package com.gmail.yevgen.spring.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import elemental.json.Json;
import net.coobird.thumbnailator.Thumbnails;

@Route("update")
@PageTitle("Update user info")
@StyleSheet("frontend://css/style.css")
public class UpdateUserView extends VerticalLayout implements HasUrlParameter<String> {
    private static final long serialVersionUID = 2659811876997659447L;
    private final PersonRepository personRepository;
    private PBEStringEncryptor passwordEncryptor;
    private Person person;

    @Autowired
    public UpdateUserView(PersonRepository personRepository, PBEStringEncryptor passwordEncryptor) {
        this.personRepository = personRepository;
        this.passwordEncryptor = passwordEncryptor;
    }

    void savePerson(Person p) {
        p.setPassword(passwordEncryptor.encrypt(p.getPassword()));
        personRepository.save(p);
    }

    boolean ifPersonWithLoginExists(Person p) {
        return personRepository.findByLogin(p.getLogin()) != null;
    }

    private void showUpdateInfoView(UUID id) {
        PersonLayout layoutWithBinder = new PersonLayout();
        H3 updateUserHeader = new H3("Update user info");
        updateUserHeader.addClassName("pageHeader");

        Binder<Person> binder = new Binder<>();
        person = personRepository.findById(id).get();

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

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Span dropLabel = new Span("drag photo here");

        upload.setUploadButton(new Button("Upload"));
        upload.setDropLabel(dropLabel);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            try {
                BufferedImage inputImage = Thumbnails.of(buffer.getInputStream()).size(128, 128).asBufferedImage();
                ByteArrayOutputStream pngContent = new ByteArrayOutputStream();
                ImageIO.write(inputImage, "png", pngContent);
                StreamResource sr = new StreamResource("", () -> {
                    return new ByteArrayInputStream(pngContent.toByteArray());
                });
                if (sr != null) {
                    sr.setContentType("image/png");
                    photo.setSrc(sr);
                    photo.setMaxHeight("128px");
                    photo.setMaxWidth("128px");
                    person.setProfilePicture(pngContent.toByteArray());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        TextField nameField = new TextField();
        nameField.setValue(person.getName());
        nameField.setValueChangeMode(ValueChangeMode.EAGER);

        TextField loginField = new TextField();
        loginField.setValue(person.getLogin());
        loginField.setValueChangeMode(ValueChangeMode.EAGER);

        PasswordField passwordField = new PasswordField();
        passwordField.setValue(passwordEncryptor.decrypt(person.getPassword()));
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setValue(passwordEncryptor.decrypt(person.getPassword()));
        confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setValue(person.getBirthDate());

        Button updateButton = new Button(" Update", VaadinIcon.USER.create());
        Button resetButton = new Button(" Reset", VaadinIcon.WARNING.create());
        Button cancelButton = new Button(" Cancel", VaadinIcon.ARROW_BACKWARD.create());

        layoutWithBinder.addPersonItem("Name:", nameField);
        layoutWithBinder.addPersonItem("Login:", loginField);
        layoutWithBinder.addPersonItem("Password:", passwordField);
        layoutWithBinder.addPersonItem("Confirm:", confirmPasswordField);
        layoutWithBinder.addPersonItem("Birthdate:", birthDatePicker);

        HorizontalLayout dialogButtonsBar = new HorizontalLayout();
        dialogButtonsBar.add(updateButton, resetButton, cancelButton);
        updateButton.getStyle().set("marginRight", "10px");
        resetButton.getStyle().set("marginRight", "10px");

        nameField.setRequiredIndicatorVisible(true);
        loginField.setRequiredIndicatorVisible(true);
        passwordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.setRequiredIndicatorVisible(true);
        birthDatePicker.setRequiredIndicatorVisible(true);

        // user input validation
        binder.forField(nameField).withValidator(new StringLengthValidator("name is mandatory", 1, null))
                .bind(Person::getName, Person::setName);

        binder.forField(loginField).withValidator(new StringLengthValidator("login is mandatory", 1, null))
                .bind(Person::getLogin, Person::setLogin);

        binder.forField(passwordField).withValidator(new StringLengthValidator("password is mandatory", 1, null))
                .bind(Person::getPassword, Person::setPassword);
        binder.forField(confirmPasswordField)
                .withValidator(new StringLengthValidator("password confirmation is mandatory", 1, null))
                .withValidator(confirm -> confirm.equals(passwordField.getValue()),
                        "password doesn't match its confirmation")
                .bind(Person::getPassword, Person::setPassword);

        Binder.Binding<Person, String> confirmationBinding = binder.forField(confirmPasswordField)
                .withValidator(confirm -> confirm.equals(passwordField.getValue()),
                        "password doesn't match its confirmation")
                .bind(Person::getPassword, Person::setPassword);
        passwordField.addValueChangeListener(event -> confirmationBinding.validate());

        binder.forField(birthDatePicker).withValidator(bd -> bd != null, "birthdate is mandatory")
                .withValidator(
                        new DateRangeValidator("birthdate out of sense", LocalDate.ofYearDay(1, 1), LocalDate.now()))
                .bind(Person::getBirthDate, Person::setBirthDate);

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        updateButton.addClickListener(event -> {
            if (binder.writeBeanIfValid(person)) {
                savePerson(person);
                dialog.close();
                UI.getCurrent().navigate("account",
                        QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(person.getId())))
                                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
            } else {
                BinderValidationStatus<Person> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                Notification.show("There are errors: " + errorText);
            }
        });
        resetButton.addClickListener(event -> {
            photo.setSrc("../frontend/img/anon.png");
            upload.getElement().setPropertyJson("files", Json.createArray());
            person.setProfilePicture(null);
            binder.readBean(null);
        });
        cancelButton.addClickListener(event -> {
            dialog.close();
            UI.getCurrent().navigate("account",
                    QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(person.getId())))
                            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        });
        add(updateUserHeader);

        VerticalLayout mainLayout = new VerticalLayout(new HorizontalLayout(photo, upload), layoutWithBinder,
                dialogButtonsBar);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);
        dialog.add(mainLayout);
        dialog.setOpened(true);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        UUID id = UUID.fromString(parametersMap.get("user").get(0));
        showUpdateInfoView(id);
    }
}
