package com.gmail.yevgen.spring.domain.repository;

import java.util.UUID;

import com.gmail.yevgen.spring.domain.Person;

public interface PersonRepository extends ExtendedJPARepository<Person, UUID> {
    Person findByLogin(String login);

    Person findByEmail(String email);
}
