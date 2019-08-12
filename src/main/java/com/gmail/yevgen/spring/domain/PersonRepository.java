package com.gmail.yevgen.spring.domain;

import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {
    Person findByLogin(String login);

    Person findByLoginAndPassword(String login, String password);
}
