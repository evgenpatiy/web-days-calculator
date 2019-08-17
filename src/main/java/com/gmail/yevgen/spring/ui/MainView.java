package com.gmail.yevgen.spring.ui;

import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.Person;
import com.gmail.yevgen.spring.domain.PersonRepository;
import com.vaadin.flow.component.HtmlComponent;
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
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@PageTitle("Days calculator")
@StyleSheet("frontend://css/style.css")
@Route("")
@Theme(Lumo.class)
@PWA(name = "Web days calculator", shortName = "daysCalc", iconPath = "../frontend/img/logo.png")
public class MainView extends VerticalLayout {
    private static final long serialVersionUID = 7657167124498205619L;
    private PersonRepository personRepository;
    private PBEStringEncryptor passwordEncryptor;

    @Autowired
    public MainView(PersonRepository personRepository, PBEStringEncryptor passwordEncryptor) {
        this.personRepository = personRepository;
        this.passwordEncryptor = passwordEncryptor;

        Button signInButton = new Button(" Sign In", VaadinIcon.SIGN_IN.create(), e -> showLoginView().setOpened(true));
        signInButton.addClassName("topButton");

        Button signUpButton = new Button(" Sign Up", VaadinIcon.USER.create(),
                event -> UI.getCurrent().navigate(NewUserView.class));
        signUpButton.addClassName("topButton");

        HorizontalLayout buttonsLine = new HorizontalLayout();
        buttonsLine.add(signInButton, signUpButton);
        buttonsLine.addClassName("topButtonsBar");

        HtmlComponent br = new HtmlComponent("br");
        Div titleMessage = new Div();
        titleMessage.addClassName("titleMessage");
        titleMessage.add(new Label("One of these days"), br);
        titleMessage.add(new Label("Pink Floyd, 1971"));

        Div pulse = new Div();
        pulse.addClassNames("p", "p-1");

        add(buttonsLine, titleMessage, pulse);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
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

    private final LoginOverlay showLoginView() {
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
            if (ifPersonWithLoginExists(event.getUsername().toLowerCase())) {
                if (ifPersonWithLoginAndPasswordExists(event.getUsername().toLowerCase(), event.getPassword())) {
                    login.close();
                    UUID id = personRepository.findByLogin(event.getUsername().toLowerCase()).getId();
                    UI.getCurrent().navigate("account",
                            QueryParameters.simple(Stream.of(new SimpleEntry<>("user", String.valueOf(id)))
                                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
                } else {
                    showErrorNotification("Wrong password for user " + event.getUsername());
                    login.setEnabled(true);
                }
            } else {
                login.close();
                showErrorNotification("User " + event.getUsername() + " not found, please sign up first");
            }
        });
        return login;
    }

    boolean ifPersonWithLoginExists(String login) {
        return personRepository.findByLogin(login.toLowerCase()) != null;
    }

    boolean ifPersonWithLoginAndPasswordExists(String login, String password) {
        Person p = personRepository.findByLogin(login);
        return p != null && password.equals(passwordEncryptor.decrypt(p.getPassword()));
    }
}
