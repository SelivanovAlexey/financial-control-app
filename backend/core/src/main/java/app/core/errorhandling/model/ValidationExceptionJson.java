package app.core.errorhandling.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record ValidationExceptionJson(String msg, Map<String, String> errors) {
}
