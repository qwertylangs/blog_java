package com.example.demo.configs;

import com.example.demo.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);


    private static final List<AntPathRequestMatcher> WHITELIST = List.of(
            new AntPathRequestMatcher("/api/login", "POST"),
            new AntPathRequestMatcher("/api/registration", "POST"),
            new AntPathRequestMatcher("/api/registration-admin", "POST"),
            new AntPathRequestMatcher("/api/articles/anonym", "GET"),
            new AntPathRequestMatcher("/api/articles/anonym/**", "GET")
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // если хоть один matcher «срабатывает», фильтр не запускается
        return WHITELIST.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = null;
        String token = null;

        LOGGER.info("Processing JWT for request to: {}", request.getRequestURI());
        var cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response); // Пропускаем дальше
            return;
        }

        token = Stream.of(cookies)
                .filter(cookie -> cookie.getName().equals("auth-token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);


        if (token == null) {
            filterChain.doFilter(request, response); // Пропускаем дальше
            return;
        }

         try {
             username = jwtTokenUtils.getUsernameFromToken(token);

             if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                 var roles = jwtTokenUtils.getRolesFromToken(token);
                 var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                         username,
                         null,
                         roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                 );

                 SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                 LOGGER.info(usernamePasswordAuthenticationToken.getAuthorities().toString());
             }
         } catch (ExpiredJwtException e) {
             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
             filterChain.doFilter(request, response);
             return;
         } catch (SignatureException e) {
             response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "InvalidToken");
             filterChain.doFilter(request, response);
             return;
         }

        filterChain.doFilter(request, response);
    }
}
