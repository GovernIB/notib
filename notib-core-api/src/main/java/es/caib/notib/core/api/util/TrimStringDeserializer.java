package es.caib.notib.core.api.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TrimStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        String value = p.getValueAsString();

        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        return value;
    }
}
