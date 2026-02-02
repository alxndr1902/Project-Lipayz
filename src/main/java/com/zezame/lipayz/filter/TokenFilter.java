package com.zezame.lipayz.filter;

import com.zezame.lipayz.pojo.AuthorizationPojo;
import com.zezame.lipayz.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {
    private final List<RequestMatcher> requestMatchers;
    private final JwtService jwtService;

    public TokenFilter(List<RequestMatcher> requestMatchers, JwtService jwtService) {
        this.requestMatchers = requestMatchers;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var matched = requestMatchers.stream()
                .anyMatch(requestMatcher -> requestMatcher.matches(request));

        if (!matched) {
            var authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            var token = authHeader.substring(7);

            try {
                var claims = jwtService.validateToken(token);

                var data = new AuthorizationPojo(claims.get("id").toString());

                var role =  claims.get("role", String.class);

                Collection<? extends GrantedAuthority> authorities =
                        Collections.singletonList(new SimpleGrantedAuthority(role));

                var auth = new UsernamePasswordAuthenticationToken(data, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                //TODO: set message response
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                            "message": "Invalid Or Expired Token"
                        }
                        """);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
