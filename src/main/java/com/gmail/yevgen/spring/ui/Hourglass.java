package com.gmail.yevgen.spring.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.ui.LoadMode;

@Tag("div")
public class Hourglass extends Component {
    private static final long serialVersionUID = -3476157302675473626L;

    public Hourglass() {
        UI.getCurrent().getPage().addHtmlImport("frontend://html/test.html", LoadMode.EAGER);
    }
}
