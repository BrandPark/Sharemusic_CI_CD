package com.brandpark.sharemusic.infra.config;

import com.brandpark.sharemusic.infra.config.db.UppercaseJdbcTokenRepository;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Authorization
        http.authorizeRequests()
                .mvcMatchers("/", "/accounts/signup", "/error", "/search").permitAll()
                .mvcMatchers(POST, "/api/v1/accounts").permitAll()
                .mvcMatchers("/api/v1/notifications/**").authenticated()
                .mvcMatchers(GET, "/api/v1/**").permitAll()
                .mvcMatchers("/api/v1/albums/*/comments/**").authenticated()
                .mvcMatchers("/api/v1/accounts/*/*follow").authenticated()
                .mvcMatchers("/api/v1/albums/**").hasRole("USER")
                .mvcMatchers(GET, "/partial/**").permitAll()
                .mvcMatchers(GET, "/accounts/*").permitAll()
                .mvcMatchers("/accounts/edit/*").authenticated()
                .mvcMatchers(GET, "/albums/*").permitAll()
                .mvcMatchers(POST, "/resend-verify-mail").hasRole("GUEST")
                .mvcMatchers("/albums/**").hasRole("USER")
                .anyRequest().authenticated();

        // login & logout
        http.formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout().logoutSuccessUrl("/").deleteCookies("JSESSIONID", "SESSION");

        // RememberMe
        http.rememberMe()
                .tokenValiditySeconds(31536000)
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**", "/custom/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        UppercaseJdbcTokenRepository jdbcTokenRepository = new UppercaseJdbcTokenRepository();
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }
}
