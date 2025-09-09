package com.service.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${auth.service.url:http://localhost:8083}")
    private String authServiceUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("Processing request for path: {}", path);
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            log.info("Skipping authentication for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Applying authentication for path: {}", path);

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("Found token in Authorization header");
        }

        // Check for token in query parameter as fallback
        if (token == null) {
            token = request.getParameter("token");
            if (token != null) {
                log.info("Found token in query parameter");
                // Set cookie for future requests to avoid page reload issues
                Cookie authCookie = new Cookie("authToken", token);
                authCookie.setPath("/");
                authCookie.setMaxAge(24 * 60 * 60); // 24 hours
                authCookie.setHttpOnly(true);
                authCookie.setSecure(false); // Set to true in production with HTTPS
                ((HttpServletResponse) response).addCookie(authCookie);
                log.info("Set authToken cookie for token found in query parameter");
            }
        }

        // Check for token in cookies as another fallback
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                log.info("Found {} cookies to check", cookies.length);
                for (Cookie cookie : cookies) {
                    log.debug("Cookie: {}={}", cookie.getName(), cookie.getValue().length() > 20 ? 
                             cookie.getValue().substring(0, 20) + "..." : cookie.getValue());
                    if ("authToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("Found token in cookie");
                        break;
                    }
                }
            } else {
                log.info("No cookies found in request");
            }
        }

        if (token == null) {
            log.warn("No token found for path: {}", path);
            // For web pages, redirect to login instead of returning 401
            if (isWebPageRequest(path)) {
                log.info("Redirecting to login for web page: {}", path);
                response.sendRedirect("http://localhost:8083/login");
                return;
            } else {
                sendUnauthorizedResponse(response, "No authentication token provided");
                return;
            }
        }

        try {
            UserValidationResponse validation = validateTokenWithAuthService(token);
            
            if (!validation.isValid()) {
                // For web pages, redirect to login instead of returning 401
                if (isWebPageRequest(path)) {
                    response.sendRedirect("http://localhost:8083/login");
                    return;
                } else {
                    sendUnauthorizedResponse(response, "Invalid authentication token");
                    return;
                }
            }

            // Add user information to request attributes
            request.setAttribute("userId", validation.getUserId());
            request.setAttribute("applicantId", validation.getUserId()); // For compatibility with ApplicationController
            request.setAttribute("userEmail", validation.getEmail());
            request.setAttribute("userName", validation.getName());
            request.setAttribute("userType", validation.getUserType());
            request.setAttribute("externalUserId", validation.getExternalUserId());

            // Set Spring Security authentication context
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    validation.getEmail(), 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error validating token with auth service", e);
            // For web pages, redirect to login instead of returning 401
            if (isWebPageRequest(path)) {
                response.sendRedirect("http://localhost:8083/login");
                return;
            } else {
                sendUnauthorizedResponse(response, "Authentication service error");
            }
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/") || 
               path.equals("/health") || 
               path.equals("/favicon.ico") ||
               path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/static/") ||
               path.startsWith("/images/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/resources/") ||
               path.equals("/login-redirect") ||
               // Cross-service communication endpoints (Job service calling Application service)
               path.matches("/api/jobs/\\d+/applications") ||
               path.endsWith(".js") ||
               path.endsWith(".css") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg") ||
               path.endsWith(".ico") ||
               path.endsWith(".gif") ||
               path.endsWith(".svg") ||
               path.endsWith(".woff") ||
               path.endsWith(".woff2") ||
               path.endsWith(".ttf") ||
               path.endsWith(".eot");
    }

    private boolean isWebPageRequest(String path) {
        return path.equals("/dashboard") || 
               path.equals("/browse-jobs") || 
               path.equals("/my-applications") || 
               path.equals("/profile");
    }

    private UserValidationResponse validateTokenWithAuthService(String token) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new TokenValidationRequest(token));
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authServiceUrl + "/api/auth/validate-token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Auth service returned status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), UserValidationResponse.class);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/html");
        response.getWriter().write(
            "<!DOCTYPE html>" +
            "<html><head><title>Authentication Required</title>" +
            "<style>body{font-family:Arial,sans-serif;text-align:center;padding:50px;}" +
            ".container{max-width:500px;margin:0 auto;}" +
            ".btn{background:#007bff;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;}</style>" +
            "</head><body>" +
            "<div class='container'>" +
            "<h2>Authentication Required</h2>" +
            "<p>" + message + "</p>" +
            "<p>Please log in to access this service.</p>" +
            "<a href='http://localhost:8083/login' class='btn'>Login</a>" +
            "</div></body></html>"
        );
    }

    @Data
    public static class TokenValidationRequest {
        private String token;
        
        public TokenValidationRequest(String token) {
            this.token = token;
        }
    }

    @Data
    public static class UserValidationResponse {
        private boolean valid;
        private Long userId;
        private String email;
        private String name;
        private String userType;
        private Long externalUserId;
        private String companyName;
    }
}
