package app.core.security;

import app.core.model.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SecurityProvider {
    void checkAccess(Long requestedResourceUserId, Long currentUserId);
    UserEntity getUserFromSecurityContext();
    void logout(HttpServletRequest request, HttpServletResponse response);
}
