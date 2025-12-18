package app.core.service;

import app.core.model.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class CommonService {
    protected UserEntity getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }

    protected void updateAuthenticationInSecurityContext(UserEntity userEntity){
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
