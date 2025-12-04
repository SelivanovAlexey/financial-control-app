package app.core.service;

import app.core.api.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final RememberMeServices rememberMeServices;

    public boolean authenticate(String username, String password, boolean rememberMe, HttpServletRequest request, HttpServletResponse response) {
        boolean authenticated = false;
        try {
            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            log.debug("Authentication attempt for user w/ username={}", username);
            Authentication authentication = authenticationManager.authenticate(authToken);
            if (authentication.isAuthenticated()) {
                SecurityContext context = securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(authentication);
                securityContextHolderStrategy.setContext(context);
                securityContextRepository.saveContext(context, request, response);

                if (rememberMe) {
                    rememberMeServices.loginSuccess(request, response, authentication);
                }

                authenticated = true;
                log.debug("User w/ username={} successfully authenticated", username);
            }
        } catch (Exception e) {
            rememberMeServices.loginFail(request, response);
            log.debug("Authentication failed for user w/ username={}", username);
            throw e;
        }
        return authenticated;
    }
}
