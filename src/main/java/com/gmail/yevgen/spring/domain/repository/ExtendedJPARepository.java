package com.gmail.yevgen.spring.domain.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExtendedJPARepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    public List<T> findByAttributeContainsText(String attributeName, String text);
}
