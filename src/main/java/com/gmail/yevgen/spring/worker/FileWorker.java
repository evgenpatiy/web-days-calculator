package com.gmail.yevgen.spring.worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class FileWorker {
    public String fileToString(String fileName) { // fast file reader
        String text = "";
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                text += line + System.lineSeparator();
            }
        } catch (IOException e) {
            System.out.println("File not read");
        }
        return text;
    }
}
