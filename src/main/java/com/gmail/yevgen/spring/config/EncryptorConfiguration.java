package com.gmail.yevgen.spring.config;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EncryptorConfiguration {
    @Autowired
    private Environment env;

    @Bean
    public PBEStringEncryptor passwordEncryptor() {
        PBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(env.getProperty("pbe.encryptor.password"));
        return encryptor;
    }
}
