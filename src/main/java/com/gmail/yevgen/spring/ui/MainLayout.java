package com.gmail.yevgen.spring.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import lombok.NoArgsConstructor;

@Theme(Lumo.class)
@StyleSheet("frontend://css/style.css")
@NoArgsConstructor
public class MainLayout extends VerticalLayout implements RouterLayout {
    private static final long serialVersionUID = 3072890241015949776L;
}