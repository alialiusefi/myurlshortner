package org.acme.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

import java.util.TimeZone;

@Singleton
public class JacksonMapperConfig implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
        mapper.setTimeZone(TimeZone.getDefault());
    }
}
