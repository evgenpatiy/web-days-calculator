package com.gmail.yevgen.spring.ui;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gmail.yevgen.spring.MainView;
import com.gmail.yevgen.spring.domain.Person;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@Route("signup")
@PageTitle("Days calculator")
public class SignUpView extends VerticalLayout {
    private static final long serialVersionUID = 2659811876997659447L;

    public SignUpView() {
        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setResponsiveSteps(new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("600px", 1, LabelsPosition.ASIDE));

        Binder<Person> binder = new Binder<>();
        Person person = new Person();

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

        Button confirmButton = new Button("Confirm");
        Button resetButton = new Button("Reset");
        Button cancelButton = new Button("Cancel");

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
                .bind(Person::getLoginName, Person::setLoginName);

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
                UI.getCurrent().navigate(DaysView.class);
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
}
