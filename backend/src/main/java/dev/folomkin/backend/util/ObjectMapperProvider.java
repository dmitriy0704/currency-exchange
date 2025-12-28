package dev.folomkin.backend.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ObjectMapperProvider
        implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()   // JavaTime, etc.
            .build();

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
