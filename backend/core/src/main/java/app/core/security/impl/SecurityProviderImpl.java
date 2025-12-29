package app.core.security.impl;

import app.core.model.UserEntity;
import app.core.security.SecurityProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityProviderImpl implements SecurityProvider {
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
    public void updateAuthenticationInSecurityContext(UserEntity userEntity) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null) {
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    userEntity, userEntity.getPassword(), userEntity.getAuthorities()
            );
            newAuth.setDetails(currentAuth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
