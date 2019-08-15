package com.gmail.yevgen.spring.domain;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    Person findByLogin(String login);

}
