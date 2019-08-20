package com.gmail.yevgen.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.gmail.yevgen.spring.domain.repository.ExtendedJPARepositoryImpl;

@Configuration
@EnableJpaRepositories(basePackages = "com.gmail.yevgen.spring.domain.repository", repositoryBaseClass = ExtendedJPARepositoryImpl.class)
public class PersonConfiguration {

}
