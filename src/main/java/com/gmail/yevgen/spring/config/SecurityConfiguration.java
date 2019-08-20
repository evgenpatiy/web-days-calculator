package com.gmail.yevgen.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.gmail.yevgen.spring.security.CustomRequestCache;
import com.gmail.yevgen.spring.security.SecurityUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login";
    private static final String ROOT_URL = "/";
    private static final String LOGOUT_SUCCESS_URL = "/";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable() // (1)

                // Register our CustomRequestCache that saves unauthorized access attempts, so
                // the user is redirected after login.
                .requestCache().requestCache(new CustomRequestCache()) // (2)

                // Restrict access to our application.
                .and().authorizeRequests()

                // Allow all flow internal requests.
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll() // (3)

                // Allow all requests by logged in users.
                .anyRequest().authenticated() // (4)

                // Configure the login page.
                .and().formLogin().loginPage(ROOT_URL).permitAll() // (5)
                .loginProcessingUrl(LOGIN_PROCESSING_URL) // (6)
                .failureUrl(LOGIN_FAILURE_URL)

                // Configure logout
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // Vaadin Flow static resources
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest", "/sw.js", "/offline-page.html",

                // icons and images
                "/icons/**", "/img/**",

                // (development mode) static resources
                "/frontend/**",

                // (development mode) webjars
                "/webjars/**",

                // (development mode) H2 debugging console
                "/h2-console/**",

                // (production mode) static resources
                "/frontend-es5/**", "/frontend-es6/**");
    }

}
