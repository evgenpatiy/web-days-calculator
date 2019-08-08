package com.gmail.yevgen.spring.ui;

import com.gmail.yevgen.spring.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("signup")
@PageTitle("SignUp")
public class SignUpView extends VerticalLayout {
    private static final long serialVersionUID = 2659811876997659447L;

    public SignUpView() {
        FormLayout nameLayout = new FormLayout();

        Button cancelButton = new Button("Cancel", e -> UI.getCurrent().navigate(MainView.class));

        TextField nameField = new TextField();
        nameField.setLabel("Name");

        TextField loginField = new TextField();
        loginField.setLabel("Login");

        TextField passwordField = new TextField();
        passwordField.setLabel("Password");

        DatePicker dayOfBirthPicker = new DatePicker();
        dayOfBirthPicker.setLabel("Day of birth");

        nameLayout.add(loginField, passwordField, nameField, dayOfBirthPicker);

        nameLayout.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("21em", 2),
                new ResponsiveStep("22em", 3), new ResponsiveStep("23em", 4));

        add(nameLayout);
        add(cancelButton);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
