package com.service.authentication.config;

import com.service.authentication.entity.User;
import com.service.authentication.entity.UserType;
import com.service.authentication.repository.UserRepository;
import com.service.authentication.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${application.service.url:http://localhost:8082}")
    private String applicationServiceUrl;

    @Value("${job.service.url:http://localhost:8081}")
    private String jobServiceUrl;

    private static final Set<String> CONSUMER_DOMAINS = Set.of(
            "gmail.com", "googlemail.com",
            "outlook.com", "hotmail.com", "live.com", "msn.com",
            "yahoo.com", "ymail.com", "rocketmail.com"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        String email = null;
        String name = null;
        String registrationId = null;
        if (principal instanceof DefaultOAuth2User oAuth2User) {
            Object emailAttr = oAuth2User.getAttributes().get("email");
            Object nameAttr = oAuth2User.getAttributes().get("name");
            email = emailAttr != null ? String.valueOf(emailAttr) : null;
            name = nameAttr != null ? String.valueOf(nameAttr) : null;
        }
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            registrationId = oauthToken.getAuthorizedClientRegistrationId();
        }

        // Special handling for GitHub: fetch primary email when not provided
        if ((email == null || email.isBlank()) && "github".equalsIgnoreCase(registrationId)) {
            String token = resolveAccessToken((OAuth2AuthenticationToken) authentication);
            if (token != null) {
                email = fetchGithubPrimaryEmail(token);
            }
        }

        String baseTarget = resolveBaseRedirectUrl(email);

        // If we have an email, issue JWT and append as query param
        if (email != null && email.contains("@")) {
            UserType inferredType = isConsumerEmail(email) ? UserType.APPLICANT : UserType.EMPLOYER;
            User user = ensureUser(email, name, inferredType);
            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserType().name(), user.getExternalUserId());
            String target = baseTarget + "/dashboard?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            response.sendRedirect(target);
            return;
        }

        // Fallback when provider didn't return email (e.g., GitHub without email scope data)
        response.sendRedirect(baseTarget);
    }

    private String resolveAccessToken(OAuth2AuthenticationToken oauthToken) {
        if (oauthToken == null) return null;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
        if (client == null || client.getAccessToken() == null) return null;
        return client.getAccessToken().getTokenValue();
    }

    private String fetchGithubPrimaryEmail(String accessToken) {
        try {
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("X-GitHub-Api-Version", "2022-11-28");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<List> response = rest.exchange(
                    "https://api.github.com/user/emails", HttpMethod.GET, entity, List.class);
            Object body = response.getBody();
            if (!(body instanceof List<?> emailsList)) return null;
            // Find primary verified email
            String candidate = null;
            for (Object item : emailsList) {
                if (item instanceof HashMap<?, ?> map) {
                    Object emailObj = map.get("email");
                    Object primaryObj = map.get("primary");
                    Object verifiedObj = map.get("verified");
                    boolean primary = primaryObj instanceof Boolean b && b;
                    boolean verified = verifiedObj instanceof Boolean b2 && b2;
                    if (emailObj != null && primary && verified) {
                        return String.valueOf(emailObj);
                    }
                    if (emailObj != null && verified && candidate == null) {
                        candidate = String.valueOf(emailObj);
                    }
                }
            }
            return candidate;
        } catch (Exception ignored) {
            return null;
        }
    }

    private User ensureUser(String email, String name, UserType inferredType) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            if (user.getName() == null && name != null) {
                user.setName(name);
            }
            if (user.getUserType() == null) {
                user.setUserType(inferredType);
            }
            return userRepository.save(user);
        }
        String randomPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());
        User user = User.builder()
                .email(email)
                .name(name != null ? name : email.substring(0, email.indexOf('@')))
                .password(randomPassword)
                .userType(inferredType)
                .build();
        return userRepository.save(user);
    }

    private String resolveBaseRedirectUrl(String email) {
        if (email == null || !email.contains("@")) {
            return jobServiceUrl; // safe default
        }
        return isConsumerEmail(email) ? applicationServiceUrl : jobServiceUrl;
    }

    private boolean isConsumerEmail(String email) {
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        return CONSUMER_DOMAINS.contains(domain);
    }
}


