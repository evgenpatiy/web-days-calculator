package com.gmail.yevgen.spring.ui;

import java.io.IOException;
import java.util.Random;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.yevgen.spring.domain.repository.PersonRepository;
import com.gmail.yevgen.spring.worker.FileWorker;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@PageTitle("Days calculator")
@Route(value = "", layout = MainLayout.class)
@PWA(name = "Web days calculator", shortName = "daysCalc", iconPath = "../frontend/img/logo.png")
public final class MainView extends VerticalLayout {
    private static final long serialVersionUID = 7657167124498205619L;
    @SuppressWarnings("unused")
    private FileWorker fileWorker;

    @Autowired
    public MainView(PersonRepository personRepository, PBEStringEncryptor passwordEncryptor, FileWorker fileWorker) {
        this.fileWorker = fileWorker;

        Button signInButton = new Button(" Sign In", VaadinIcon.SIGN_IN.create(),
                event -> UI.getCurrent().navigate(LoginView.class));
        signInButton.addClassName("topButton");

        Button signUpButton = new Button(" Sign Up", VaadinIcon.USER.create(),
                event -> UI.getCurrent().navigate(NewUserView.class));
        signUpButton.addClassName("topButton");

        HorizontalLayout buttonsLine = new HorizontalLayout();
        buttonsLine.add(signInButton, signUpButton);
        buttonsLine.addClassName("topButtonsBar");

        Label text = new Label();
        text.addClassName("wrapLabel");
        try {
            text.setText(fileWorker.fileToString("text/" + new Random().nextInt(10) + ".txt"));
        } catch (IOException e) {
        }
        text.setEnabled(false);

        Div pulse1 = new Div();
        pulse1.addClassNames("p", "p-3");
        Div pulse2 = new Div();
        pulse2.addClassNames("p", "p-2");
        Div pulse3 = new Div();
        pulse3.addClassNames("p", "p-1");

        add(buttonsLine, text, pulse1, pulse2, pulse3);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }
}
