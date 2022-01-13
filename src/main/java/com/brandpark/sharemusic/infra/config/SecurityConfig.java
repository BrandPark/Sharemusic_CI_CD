package com.brandpark.sharemusic.infra.config;

import com.brandpark.sharemusic.infra.config.db.UppercaseJdbcTokenRepository;
import com.brandpark.sharemusic.infra.jwt.JwtRequestFilter;
import com.brandpark.sharemusic.modules.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // 자격 증명을 매칭하기 위한 user가 어디에서 로드되는지 알기 위해
        // AuthenticationManager 를 설정한다.
        // BCryptPasswordEncoder 를 사용한다.
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Authorization
        http.authorizeRequests()
                .mvcMatchers("/", "/accounts/signup", "/error", "/search").permitAll()
                .mvcMatchers( "/authenticate").permitAll()
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

        // Filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
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
