package app.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonHelper {

    private final ObjectMapper mapper;

    public <K, V> JsonNode createJson(Map<K, V> inputMap) {
        return mapper.valueToTree(inputMap);
    }
}
