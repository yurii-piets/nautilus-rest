package com.nautilus.converter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeConverter extends AbstractJsonConverter<LocalDateTime> {

    @Override
    public LocalDateTime toEntityAttribute(String value) {
        try {
            return mapper.readValue(value, LocalDateTime.class);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return null;
        }
    }
}
