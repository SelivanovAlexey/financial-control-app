package app.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "api.root.tag", description = "api.root.tag.description")
public class RootController {

    private final MessageSource messageSource;

    @Value("${springdoc.api-docs.enabled:true}")
    private boolean apiDocsEnabled;

    @Operation(summary = "api.root.info")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getApiInfo() {
        return getMetadata();
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String getApiPortal(Model model) {
        // Thymeleaf uses #{key} for localized messages from messages.properties
        // automatically
        // so we only need to pass the dynamic data (version, status, flags, links)

        model.addAttribute("version", "1.0.0"); // In a real app, this could come from build properties
        model.addAttribute("status", "UP");
        model.addAttribute("apiDocsEnabled", apiDocsEnabled);

        if (apiDocsEnabled) {
            model.addAttribute("docs_swagger", "/swagger-ui.html");
            model.addAttribute("docs_scalar", "/scalar");
            model.addAttribute("api_json", "/v3/api-docs");
        }
        model.addAttribute("health", "/internal/health");

        return "apiInfo";
    }

    private Map<String, Object> getMetadata() {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("name", messageSource.getMessage("openapi.title", null, locale));
        response.put("version", messageSource.getMessage("openapi.version", null, locale));
        response.put("description", messageSource.getMessage("openapi.description", null, locale));
        response.put("status", "UP");

        Map<String, String> links = new LinkedHashMap<>();
        if (apiDocsEnabled) {
            links.put("docs_swagger", "/swagger-ui.html");
            links.put("docs_scalar", "/scalar");
            links.put("api_json", "/v3/api-docs");
        }
        links.put("health", "/internal/health");

        response.put("_links", links);
        return response;
    }
}
