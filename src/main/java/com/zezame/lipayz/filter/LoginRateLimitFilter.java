package com.zezame.lipayz.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zezame.lipayz.dto.ErrorResDTO;
import com.zezame.lipayz.exceptiohandler.exception.RateLimitException;
import com.zezame.lipayz.util.RateLimiterUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final RateLimiterUtil rateLimiterUtil;
    private final List<RequestMatcher> requestMatchers;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean matched = requestMatchers.stream()
                    .anyMatch(requestMatcher -> requestMatcher.matches(request));

            if (!matched) {
                filterChain.doFilter(request, response);
                return;
            }

            String ip = request.getRemoteAddr();

            String key = ip + ":" + request.getRequestURI();

            validateRateLimit(key);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(responseJSON(e.getMessage()));
        }
    }

    private void validateRateLimit(String key) {
        if (!rateLimiterUtil.tryConsume(key)) {
            Duration wait = rateLimiterUtil.onRateLimited(key);

            String formatted = String.format("%02d:%02d",
                    wait.toMinutesPart(),
                    wait.toSecondsPart());

            throw new RateLimitException(
                    "Please Try Again After " + formatted + " Minutes"
            );
        }
    }


    private String responseJSON(String message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new ErrorResDTO<>(message));
    }
}
