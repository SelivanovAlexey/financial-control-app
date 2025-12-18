package app.core.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.access.AccessDeniedException;

@UtilityClass
public class SecurityUtils {
    public static void checkAccess(Long requestedResourceUserId, Long currentUserId){
        if (!currentUserId.equals(requestedResourceUserId)) {
            throw new AccessDeniedException("Access to this resource is not allowed for current user");
        }
    }
}
