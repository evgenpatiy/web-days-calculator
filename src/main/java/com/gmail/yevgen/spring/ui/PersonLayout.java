package com.gmail.yevgen.spring.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PersonLayout extends Composite<FormLayout> {
    private static final long serialVersionUID = 1568723488300942405L;

    public void addPersonItem(String label, Component... items) {
        Div itemWrapper = new Div();
        itemWrapper.add(items);
        getContent().addFormItem(itemWrapper, label);
    }
}
