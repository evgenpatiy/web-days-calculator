package com.gmail.yevgen.spring.ui;

import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@Route(value = LoginView.ROUTE, layout = MainLayout.class)
@PageTitle("User login")
public class LoginView extends VerticalLayout {
    private static final long serialVersionUID = -3319481239574370949L;
    public static final String ROUTE = "login";
    private PersonRepository personRepository;
    private PBEStringEncryptor passwordEncryptor;

    @Autowired
    public LoginView(PersonRepository personRepository, PBEStringEncryptor passwordEncryptor) {
        this.personRepository = personRepository;
        this.passwordEncryptor = passwordEncryptor;

        Label viewDetailsHeader = new Label("Authentication");
        viewDetailsHeader.addClassName("pageHeader");
        add(viewDetailsHeader);

        LoginForm login = new LoginForm();
        login.setForgotPasswordButtonVisible(false);
        login.addLoginListener(event -> {
            if (ifPersonWithLoginExists(event.getUsername().toLowerCase())) {
                if (ifPersonWithLoginAndPasswordExists(event.getUsername().toLowerCase(), event.getPassword())) {
                    UUID id = personRepository.findByLogin(event.getUsername().toLowerCase()).getId();
                    UI.getCurrent().navigate("account",
                            QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(id)))
                                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
                } else {
                    login.setError(true);
                }
            } else {
                showErrorNotification("User " + event.getUsername() + " not found, please sign up first");
                UI.getCurrent().navigate(MainView.class);
            }
        });

        setAlignSelf(Alignment.CENTER, login);
        add(login);
    }

    private final void showErrorNotification(String notificationText) {
        Div content = new Div();
        content.addClassName("errorNotification");
        content.setText(notificationText);

        Notification wrongLoginNotification = new Notification(content);
        wrongLoginNotification.setDuration(3000);
        wrongLoginNotification.setPosition(Position.MIDDLE);
        wrongLoginNotification.open();
    }

    boolean ifPersonWithLoginExists(String login) {
        return personRepository.findByLogin(login.toLowerCase()) != null;
    }

    boolean ifPersonWithLoginAndPasswordExists(String login, String password) {
        Person p = personRepository.findByLogin(login);
        return p != null && password.equals(passwordEncryptor.decrypt(p.getPassword()));
    }
}
