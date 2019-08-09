package com.gmail.yevgen.spring.data;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Service
public class Person {
    private String name;
    private String loginName;
    private String password;
    private LocalDate birthDate;
}
