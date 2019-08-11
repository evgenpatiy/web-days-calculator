package com.gmail.yevgen.spring.repository;

import org.springframework.data.repository.CrudRepository;

import com.gmail.yevgen.spring.domain.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {

}
