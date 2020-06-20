package br.com.zecode.backendchallenge.model.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointDeserializer extends JsonDeserializer<GeoJsonPoint> {

    @Override
    public GeoJsonPoint deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.get("type").asText();
        JsonNode node1 = node.get("coordinates");
        Double x = node1.get(0).asDouble();
        Double y = node1.get(1).asDouble();

        GeoJsonPoint returned = new GeoJsonPoint(x, y);

        if (returned.getType().equals(type)) {
            return returned;
        } else {
            return null;
        }

    }
}
