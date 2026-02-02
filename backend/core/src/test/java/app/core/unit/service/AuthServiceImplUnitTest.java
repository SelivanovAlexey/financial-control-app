package app.core.unit.service;

import app.core.model.dto.AuthRequestDto;
import app.core.service.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Unit Tests")
@ActiveProfiles("unit")
class AuthServiceImplUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private RememberMeServices rememberMeServices;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("Should authenticate successfully when valid credentials provided")
    void shouldAuthenticateSuccessfully() {
        // Given
        AuthRequestDto authRequest = new AuthRequestDto("testuser", "password123", false);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::createEmptyContext).thenReturn(mockSecurityContext);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);

            // When
            authService.authenticate(authRequest, request, response);

            // Then
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(mockSecurityContext).setAuthentication(authentication);
            verify(securityContextRepository).saveContext(mockSecurityContext, request, response);

            verify(rememberMeServices, never()).loginSuccess(any(), any(), any());
        }
    }

    @Test
    @DisplayName("Should authenticate with remember me when rememberMe is true")
    void shouldAuthenticateWithRememberMe() {
        // Given
        AuthRequestDto authRequest = new AuthRequestDto("testuser", "password123", true);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::createEmptyContext).thenReturn(mockSecurityContext);

            when(authenticationManager.authenticate(any()))
                    .thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);

            // When
            authService.authenticate(authRequest, request, response);

            // Then
            verify(mockSecurityContext).setAuthentication(authentication);
            verify(securityContextRepository).saveContext(mockSecurityContext, request, response);
            verify(rememberMeServices).loginSuccess(request, response, authentication);
        }
    }

    @Test
    @DisplayName("Should handle authentication failure")
    void shouldHandleAuthenticationFailure() {
        // Given
        AuthRequestDto authRequest = new AuthRequestDto("testuser", "wrongpassword", false);
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest, request, response))
                .isEqualTo(exception);

        verify(rememberMeServices).loginFail(request, response);
        verify(securityContextRepository, never()).saveContext(any(), any(), any());
    }

    @Test
    @DisplayName("Should not set security context when authentication fails")
    void shouldNotSetSecurityContextWhenAuthenticationFails() {
        // Given
        AuthRequestDto authRequest = new AuthRequestDto("testuser", "password123", false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false); // Не аутентифицирован

        // When
        authService.authenticate(authRequest, request, response);

        // Then
        verify(securityContextRepository, never()).saveContext(any(), any(), any());
        verify(rememberMeServices, never()).loginSuccess(any(), any(), any());
    }
}