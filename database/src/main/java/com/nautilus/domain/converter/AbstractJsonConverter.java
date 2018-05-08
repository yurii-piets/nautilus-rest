package com.nautilus.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.ogm.typeconversion.AttributeConverter;

public abstract class AbstractJsonConverter<T> implements AttributeConverter<T, String> {

    protected ObjectMapper mapper = new ObjectMapper();

    protected Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public String toGraphProperty(T value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.error("Unexpected: ", e);
            return null;
        }
    }
}
