package app.core.errorhandling.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record ValidationExceptionJson(Map<String, String> errors, String msg) {
}
