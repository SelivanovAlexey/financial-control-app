package app.core.errorhandling.model;

import lombok.Builder;

@Builder
public record CommonExceptionJson(String msg, String cause) {
}
