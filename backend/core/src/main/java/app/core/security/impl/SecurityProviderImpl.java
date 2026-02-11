package app.core.security.impl;

import app.core.model.UserEntity;
import app.core.security.SecurityProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityProviderImpl implements SecurityProvider {

    private final List<LogoutHandler> logoutHandlers;

    @Override
    public void checkAccess(Long requestedResourceUserId, Long currentUserId) {
        if (!currentUserId.equals(requestedResourceUserId)) {
            throw new AccessDeniedException("Access to this resource is not allowed for current user");
        }
    }

    @Override
    public UserEntity getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (LogoutHandler handler : logoutHandlers) {
            handler.logout(request, response, auth);
        }
    }
}
