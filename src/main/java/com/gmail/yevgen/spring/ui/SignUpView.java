package com.gmail.yevgen.spring.ui;

import java.util.Optional;
import java.util.stream.Collectors;

import com.gmail.yevgen.spring.MainView;
import com.gmail.yevgen.spring.data.Person;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("signup")
@PageTitle("Sign up to days calculator")
public class SignUpView extends VerticalLayout {
    private static final long serialVersionUID = 2659811876997659447L;

    public SignUpView() {
        FormLayout layoutWithBinder = new FormLayout();
        Binder<Person> binder = new Binder<>();
        Person person = new Person();

        TextField name = new TextField();
        name.setValueChangeMode(ValueChangeMode.EAGER);

        TextField loginName = new TextField();
        loginName.setValueChangeMode(ValueChangeMode.EAGER);

        PasswordField password = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.EAGER);

        DatePicker birthDate = new DatePicker();
        Label infoLabel = new Label();

        Button confirmButton = new Button("Confirm");
        Button resetButton = new Button("Reset");
        Button cancelButton = new Button("Cancel");

        layoutWithBinder.addFormItem(name, "Name");
        layoutWithBinder.addFormItem(loginName, "Login");
        layoutWithBinder.addFormItem(birthDate, "Birthdate");
        layoutWithBinder.addFormItem(password, "Password");

        HorizontalLayout buttonsLine = new HorizontalLayout();
        buttonsLine.add(confirmButton, resetButton, cancelButton);
        confirmButton.getStyle().set("marginRight", "10px");
        resetButton.getStyle().set("marginRight", "10px");

        name.setRequiredIndicatorVisible(true);
        loginName.setRequiredIndicatorVisible(true);
        password.setRequiredIndicatorVisible(true);
        birthDate.setRequiredIndicatorVisible(true);

        binder.forField(name).withValidator(new StringLengthValidator("Your name is mandatory!", 1, null))
                .bind(Person::getName, Person::setName);
        binder.forField(loginName).withValidator(new StringLengthValidator("Login is mandatory!", 1, null))
                .bind(Person::getLoginName, Person::setLoginName);
        binder.forField(password).withValidator(new StringLengthValidator("Password is mandatory!", 1, null))
                .bind(Person::getPassword, Person::setPassword);

        // Birthdate and doNotCall don't need any special validators
        // binder.bind(birthDate, Person::getBirthDate, Person::setBirthDate);

        // Click listeners for the buttons
        confirmButton.addClickListener(event -> {
            if (binder.writeBeanIfValid(person)) {
                infoLabel.setText("Saved bean values: " + person);
            } else {
                BinderValidationStatus<Person> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct().collect(Collectors.joining(", "));
                infoLabel.setText("There are errors: " + errorText);
            }
        });
        resetButton.addClickListener(event -> {
            // clear fields by setting null
            binder.readBean(null);
            infoLabel.setText("");
        });
        cancelButton.addClickListener(event -> UI.getCurrent().navigate(MainView.class));
        add(layoutWithBinder, buttonsLine);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
