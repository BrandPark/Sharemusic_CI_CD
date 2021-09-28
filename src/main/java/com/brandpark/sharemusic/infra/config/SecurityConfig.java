package com.brandpark.sharemusic.infra.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Authentication
        http.authorizeRequests()
                .mvcMatchers("/", "/accounts/signup").permitAll();

        // Role
        http.authorizeRequests()
                .mvcMatchers("/verify-email-result").hasRole("USER");

        // login & logout
        http.formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout().logoutSuccessUrl("/");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**", "/custom/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
