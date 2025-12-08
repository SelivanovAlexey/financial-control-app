package app.core.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void authenticate(String username, String password, boolean rememberMe, HttpServletRequest request, HttpServletResponse response);
}
