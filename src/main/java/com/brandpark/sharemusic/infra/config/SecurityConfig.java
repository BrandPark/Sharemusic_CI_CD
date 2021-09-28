package com.brandpark.sharemusic.infra.config;

import com.brandpark.sharemusic.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

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

        // RememberMe
        http.rememberMe()
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**", "/custom/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }
}
