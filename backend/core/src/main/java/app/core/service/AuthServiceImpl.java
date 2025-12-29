package app.core.service;

import app.core.api.AuthService;
import app.core.model.dto.AuthRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    private final RememberMeServices rememberMeServices;

    public void authenticate(AuthRequestDto authRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(authRequest.username(), authRequest.password());
            log.debug("Authentication attempt for user w/ username={}", authRequest.username());
            Authentication authentication = authenticationManager.authenticate(authToken);
            if (authentication.isAuthenticated()) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                securityContextRepository.saveContext(context, request, response);

                if (authRequest.rememberMe()) {
                    rememberMeServices.loginSuccess(request, response, authentication);
                }

                log.debug("User w/ username={} successfully authenticated", authRequest.username());
            }
        } catch (Exception e) {
            rememberMeServices.loginFail(request, response);
            log.debug("Authentication failed for user w/ username={}", authRequest.username());
            throw e;
        }
    }
}
