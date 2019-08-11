package com.gmail.yevgen.spring.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.gmail.yevgen.spring.repository.PersonRepository;

@Controller
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

}
