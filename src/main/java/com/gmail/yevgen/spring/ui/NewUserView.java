package com.gmail.yevgen.spring.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.repository.PersonRepository;
import com.gmail.yevgen.spring.worker.ActivationWorker;
import com.gmail.yevgen.spring.worker.MailWorker;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import elemental.json.Json;
import net.coobird.thumbnailator.Thumbnails;

@Route(value = NewUserView.ROUTE, layout = MainLayout.class)
@PageTitle("New user registration")
public class NewUserView extends VerticalLayout {
    private static final long serialVersionUID = 2659811876997659447L;
    public static final String ROUTE = "signup";
    private final PersonRepository personRepository;
    private PBEStringEncryptor passwordEncryptor;
    private Person person;

    @Autowired
    public NewUserView(PersonRepository personRepository, PBEStringEncryptor passwordEncryptor, MailWorker mailWorker,
            ActivationWorker activationWorker) {
        this.personRepository = personRepository;
        this.passwordEncryptor = passwordEncryptor;

        PersonLayout layoutWithBinder = new PersonLayout();
        Icon icon = VaadinIcon.USER.create();
        icon.addClassName("headerIcon");
        Span newUserHeader = new Span(icon, new Label(" New user registration"));
        newUserHeader.addClassName("pageHeader");

        Binder<Person> binder = new Binder<>();
        person = new Person();
        person.setActive(false);

        Image photo = new Image();
        photo.setSrc("frontend/img/anon.png");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.setUploadButton(new NativeButton("Upload photo"));
        upload.setDropAllowed(false);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            try {
                upload.getElement().setPropertyJson("files", Json.createArray());
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
        nameField.setClearButtonVisible(true);
        nameField.setValueChangeMode(ValueChangeMode.EAGER);

        TextField loginField = new TextField();
        loginField.setClearButtonVisible(true);
        loginField.setValueChangeMode(ValueChangeMode.EAGER);

        PasswordField passwordField = new PasswordField();
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        DatePicker birthDatePicker = new DatePicker();

        EmailField emailField = new EmailField();
        emailField.setClearButtonVisible(true);
        emailField.setErrorMessage("Please enter a valid email address");

        Button registerUserButton = new Button("Register");
        Button resetButton = new Button("Reset");
        Button cancelButton = new Button("Cancel");

        layoutWithBinder.addPersonItem("Name:", nameField);
        layoutWithBinder.addPersonItem("Login:", loginField);
        layoutWithBinder.addPersonItem("Password:", passwordField);
        layoutWithBinder.addPersonItem("Confirm:", confirmPasswordField);
        layoutWithBinder.addPersonItem("Birthdate:", birthDatePicker);
        layoutWithBinder.addPersonItem("Email:", emailField);

        HorizontalLayout dialogButtonsBar = new HorizontalLayout();
        dialogButtonsBar.add(registerUserButton, resetButton, cancelButton);
        registerUserButton.getStyle().set("marginRight", "10px");
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

        binder.forField(emailField).withValidator(new EmailValidator("correct email is mandatory"))
                .bind(Person::getEmail, Person::setEmail);

        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        registerUserButton.addClickListener(event -> {
            if (binder.writeBeanIfValid(person)) {
                if (ifPersonWithLoginExists(person)) {
                    Div content = new Div();
                    content.addClassName("errorNotification");
                    content.setText("User " + person.getLogin() + " already exists. Choose different login");

                    Notification wrongLoginNotification = new Notification(content);
                    wrongLoginNotification.setDuration(3000);
                    wrongLoginNotification.setPosition(Position.MIDDLE);
                    wrongLoginNotification.open();
                } else if (ifPersonWithEmailExists(person)) {
                    Div content = new Div();
                    content.addClassName("errorNotification");
                    content.setText("E-mail " + person.getEmail() + " already registered. Choose different e-mail");

                    Notification wrongLoginNotification = new Notification(content);
                    wrongLoginNotification.setDuration(3000);
                    wrongLoginNotification.setPosition(Position.MIDDLE);
                    wrongLoginNotification.open();
                } else {
                    person.setActivationCode(activationWorker.getActivationCode());

                    mailWorker.sendMail(person.getEmail(), "WebDays calculator activation code",
                            "Your activation code: " + String.valueOf(person.getActivationCode()));

                    Dialog confirmDialog = new Dialog();
                    Label confirmDialogLabel = new Label("Check e-mail and confirm activation code you got");
                    TextField activationCodeTextField = new TextField();
                    activationCodeTextField.setClearButtonVisible(true);
                    activationCodeTextField.setRequired(true);
                    activationCodeTextField.setSizeFull();
                    Button confirmActivationCodeButton = new Button("Confirm", e -> {
                        if (String.valueOf(person.getActivationCode())
                                .equalsIgnoreCase(activationCodeTextField.getValue())) {
                            person.setActive(true);
                            try {
                                savePerson(person);
                            } catch (InvalidDataAccessResourceUsageException ex) {
                                Notification.show("Database error!", 3000, Position.MIDDLE);
                            }
                            confirmDialog.close();
                            dialog.close();
                            Notification.show("Person " + person.getName() + " succesfully created!");
                            UI.getCurrent().navigate("account",
                                    QueryParameters.simple(Stream
                                            .of(new SimpleEntry<>("user", String.valueOf(person.getId())))
                                            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
                        } else {
                            Notification.show("Wrong activation code");
                        }
                    });
                    Button cancelActivationCodeButton = new Button("Cancel", e -> {
                        confirmDialog.close();
                    });
                    HorizontalLayout confirmDialogButtonsBar = new HorizontalLayout(confirmActivationCodeButton,
                            cancelActivationCodeButton);
                    VerticalLayout confirmDialogLayout = new VerticalLayout(confirmDialogLabel, activationCodeTextField,
                            confirmDialogButtonsBar);
                    confirmDialogLayout.setAlignItems(Alignment.CENTER);
                    confirmDialog.add(confirmDialogLayout);
                    confirmDialog.setCloseOnEsc(false);
                    confirmDialog.setCloseOnOutsideClick(false);
                    confirmDialog.open();
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
            photo.setSrc("frontend/img/anon.png");
            upload.getElement().setPropertyJson("files", Json.createArray());
            person.setProfilePicture(null);
            binder.readBean(null);
        });
        cancelButton.addClickListener(event -> {
            dialog.close();
            UI.getCurrent().navigate(MainView.class);
        });
        add(newUserHeader);

        VerticalLayout mainLayout = new VerticalLayout(photo, upload, layoutWithBinder, dialogButtonsBar);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.setAlignItems(Alignment.CENTER);
        dialog.add(mainLayout);
        dialog.setOpened(true);
    }

    private final void savePerson(Person p) {
        p.setPassword(passwordEncryptor.encrypt(p.getPassword()));
        p.setLogin(p.getLogin().toLowerCase());
        personRepository.save(p);
    }

    private final boolean ifPersonWithLoginExists(Person p) {
        return personRepository.findByLogin(p.getLogin()) != null;
    }

    private final boolean ifPersonWithEmailExists(Person p) {
        return personRepository.findByEmail(p.getEmail()) != null;
    }
}
