package com.gmail.yevgen.spring.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("dayspanel")
@PageTitle("Days calculator - my panel")
@StyleSheet("../frontend/css/style.css")
public class DaysView extends VerticalLayout {
    private static final long serialVersionUID = -3227439462230694954L;

    public DaysView() {
        Text text = new Text("my days panel!");
        add(text);
    }
}
