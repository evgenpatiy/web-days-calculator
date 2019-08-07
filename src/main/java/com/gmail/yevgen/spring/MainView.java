package com.gmail.yevgen.spring;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@PageTitle("Days calculator")
@StyleSheet("../frontend/css/login.css")
@Route
@PWA(name = "Project Base for Vaadin Flow with Spring", shortName = "Project Base")
public class MainView extends VerticalLayout {
    private static final long serialVersionUID = 7657167124498205619L;

    public MainView(@Autowired MessageBean bean) {
        showMainPage();
    }

    private final void showMainPage() {
        Button signInButton = new Button(" Sign In", e -> showLoginForm().setOpened(true));
        Icon signInIcon = VaadinIcon.SIGN_IN.create();
        signInButton.setMaxWidth("10em");
        signInButton.setIcon(signInIcon);

        Button signUpButton = new Button(" Sign Up");
        Icon signUpIcon = VaadinIcon.USER.create();
        signUpButton.setMaxWidth("10em");
        signUpButton.setIcon(signUpIcon);

        FormLayout nameLayout = new FormLayout(signInButton, signUpButton);
        nameLayout.addClassName("mainWindow");
        nameLayout.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("21em", 2));
        add(nameLayout);
    }

    private final void showWrongLoginNotification(String notificationText) {
        Div content = new Div();
        content.addClassName("wrongLoginNotification");
        content.setText(notificationText);

        Notification wrongLoginNotification = new Notification(content);
        wrongLoginNotification.setDuration(3000);
        wrongLoginNotification.setPosition(Position.MIDDLE);
        wrongLoginNotification.open();
    }

    private final LoginOverlay showLoginForm() {
        H1 title = new H1();
        title.addClassName("loginTitle");

        Icon icon = VaadinIcon.HOURGLASS.create();
        icon.addClassName("loginIcon");
        title.add(icon);
        title.add(new Text(" Days calculator"));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setAdditionalInformation("Provide non-empty login and password");
        LoginOverlay login = new LoginOverlay();
        login.setForgotPasswordButtonVisible(false);
        login.setTitle(title);
        login.setDescription("How many days you're lived already");
        login.setI18n(i18n);
        login.addLoginListener(e -> {
            login.close();
            showWrongLoginNotification("User not found, please sign up first");
        });
        return login;
    }
}
