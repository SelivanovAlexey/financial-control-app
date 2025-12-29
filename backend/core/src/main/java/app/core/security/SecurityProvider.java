package app.core.security;

import app.core.model.UserEntity;

public interface SecurityProvider {
    void checkAccess(Long requestedResourceUserId, Long currentUserId);
    UserEntity getUserFromSecurityContext();
    void updateAuthenticationInSecurityContext(UserEntity userEntity);
}
