package app.core.unit.controller;

import app.core.controller.RootController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RootControllerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RootController rootController;

    @Test
    void getApiInfo_ShouldReturnCorrectMetadata() {
        // Arrange
        String title = "Financial Control API";
        String version = "1.0.0";
        String description = "API description";

        org.springframework.test.util.ReflectionTestUtils.setField(rootController, "apiDocsEnabled", true);

        when(messageSource.getMessage(eq("openapi.title"), any(), any(Locale.class))).thenReturn(title);
        when(messageSource.getMessage(eq("openapi.version"), any(), any(Locale.class))).thenReturn(version);
        when(messageSource.getMessage(eq("openapi.description"), any(), any(Locale.class)))
                .thenReturn(description);

        // Act
        Map<String, Object> result = rootController.getApiInfo();

        // Assert
        assertEquals(title, result.get("name"));
        assertEquals(version, result.get("version"));
        assertEquals(description, result.get("description"));
        assertEquals("UP", result.get("status"));

        @SuppressWarnings("unchecked")
        Map<String, String> links = (Map<String, String>) result.get("_links");
        assertNotNull(links);
        assertEquals("/swagger-ui.html", links.get("docs_swagger"));
        assertEquals("/scalar", links.get("docs_scalar"));
        assertEquals("/v3/api-docs", links.get("api_json"));
        assertEquals("/internal/health", links.get("health"));
    }
}
