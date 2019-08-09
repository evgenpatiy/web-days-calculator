package com.gmail.yevgen.spring.workers;

import com.vaadin.flow.component.html.Image;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageWorker {
    private @NonNull Image image;

    public Image getFormattedImage() {
        return image;
    }

}
