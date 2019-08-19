package com.gmail.yevgen.spring.ui;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;

@ParentLayout(MainLayout.class)
@PageTitle("Page not found")
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {
    private static final long serialVersionUID = -7335258922000659650L;
    private Paragraph error = new Paragraph();

    public NotFoundView() {
        setAlignSelf(Alignment.CENTER, error);
        error.addClassName("loginTitle");
        add(error);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        error.setText("Can not find URL: " + event.getLocation().getPath());
        return HttpServletResponse.SC_NOT_FOUND;
    }

}
