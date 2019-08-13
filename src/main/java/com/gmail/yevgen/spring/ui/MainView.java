package com.gmail.yevgen.spring.ui;

import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@PageTitle("Days calculator")
@StyleSheet("../frontend/css/style.css")
@Route("")
@PWA(name = "Web days calculator", shortName = "dayscalc", iconPath = "../frontend/img/logo.png")
public class MainView extends VerticalLayout {
    private static final long serialVersionUID = 7657167124498205619L;
    private PersonRepository personRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MainView(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;

        Button signInButton = new Button(" Sign In", VaadinIcon.SIGN_IN.create(), e -> showLoginForm().setOpened(true));
        signInButton.setMaxWidth("10em");
        signInButton.getStyle().set("marginRight", "10px");

        Button signUpButton = new Button(" Sign Up", VaadinIcon.USER.create(),
                event -> UI.getCurrent().navigate(SignUpView.class));
        signUpButton.setMaxWidth("10em");

        HorizontalLayout buttonsLine = new HorizontalLayout();
        buttonsLine.add(signInButton, signUpButton);

        Label messageLabel = new Label("Don't waste your time");
        add(buttonsLine, messageLabel);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }

    private final void showWrongLoginNotification(String notificationText) {
        Div content = new Div();
        content.addClassName("errorNotification");
        content.setText(notificationText);

        Notification wrongLoginNotification = new Notification(content);
        wrongLoginNotification.setDuration(3000);
        wrongLoginNotification.setPosition(Position.MIDDLE);
        wrongLoginNotification.open();
    }

    private final LoginOverlay showLoginForm() {
        H2 title = new H2();
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
        login.addLoginListener(event -> {
            if (ifPersonWithLoginExists(event.getUsername())) {
                if (ifPersonWithLoginAndPasswordExists(event.getUsername(), event.getPassword())) {
                    login.close();
                    UI.getCurrent().navigate("dayspanel",
                            QueryParameters.simple(Stream.of(new SimpleEntry<>("user", event.getUsername()))
                                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
                } else {
                    showWrongLoginNotification("Wrong password for user " + event.getUsername());
                    login.setEnabled(true);
                }
            } else {
                login.close();
                showWrongLoginNotification("User not found, please sign up first");
            }
        });
        return login;
    }

    boolean ifPersonWithLoginExists(String login) {
        return personRepository.findByLogin(login) != null;
    }

    boolean ifPersonWithLoginAndPasswordExists(String login, String password) {
        Person p = personRepository.findByLogin(login);
        return p != null && passwordEncoder.matches(password, p.getPassword());
    }
}