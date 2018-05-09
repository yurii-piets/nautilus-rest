package com.nautilus.converter;

import javax.xml.stream.Location;
import java.io.IOException;

public class LocationConverter extends AbstractJsonConverter<Location> {

    // TODO: 08/05/2018 try to generify this method by readValue(value, Lccation.class);
    @Override
    public Location toEntityAttribute(String value) {
        try {
            return mapper.readValue(value, Location.class);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return null;
        }
    }
}
