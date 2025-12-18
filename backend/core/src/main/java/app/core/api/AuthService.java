package app.core.api;

import app.core.model.dto.AuthRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void authenticate(AuthRequestDto authRequest, HttpServletRequest request, HttpServletResponse response);
}
