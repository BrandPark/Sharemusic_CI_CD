package com.brandpark.sharemusic.infra.jwt;

import com.brandpark.sharemusic.infra.config.auth.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtRequestFilter 는 Spring Web Filter 인 OncePerRequestFilter 를 확장하여 들어오는 모든 요청에 대해
 * 필터 클래스가 작용합니다. 요청에 유효한 JWT 토큰이 있는지 확인하고
 * 있다면 수동으로 컨텍스트에 Authentication 을 설정하여 현재 사용자가 인증되었음을 지정합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        //JWT Token은 "Bearer {token}" 과 같은 형식이다. "Bearer"을 제거하여  토큰만 얻어낸다.
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("JWT 토큰을 얻을 수 없습니다.");
            } catch (ExpiredJwtException e) {
                log.error("JWT 토큰이 만료되었습니다.");
            }
        } else {
            log.warn("JWT 토큰이 \"Bearer\"로 시작하지 않습니다.");
        }

        // 토큰을 받으면 유효성 검사를 한다.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            CustomUserDetails userDetails = (CustomUserDetails) this.userDetailsService.loadUserByUsername(username);

            // 토큰이 유효하다면 수동으로 Authentication 을 세팅 하도록 SpringSecurity 를 설정한다.
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getSessionAccount().getPassword(), userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Authentication 을 SecurityContext 에 설정하고나면 사용자를 인증하는것에 성공한다.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
