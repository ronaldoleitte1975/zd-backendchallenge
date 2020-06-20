package br.com.zecode.backendchallenge.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonMultiPolygonDeserializer extends JsonDeserializer<GeoJsonMultiPolygon> {

    @Override
    public GeoJsonMultiPolygon deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        List<Point> listPoints = new ArrayList<>();
        List<GeoJsonPolygon> listPolignos = new ArrayList<>();
        GeoJsonMultiPolygon retorned = null;

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.get("type").asText();
        JsonNode node1 = node.get("coordinates");
        if (node1.getNodeType().equals(JsonNodeType.ARRAY)) {
            for (JsonNode jn : node1) {
                if (jn.getNodeType().equals(JsonNodeType.ARRAY)) {
                    for (JsonNode jn1 : jn) {
                        if (jn1.getNodeType().equals(JsonNodeType.ARRAY)) {
                            for (JsonNode jn2 : jn1) {
                                Point geoPoint = new Point(jn2.get(0).asDouble(), jn2.get(1).asDouble());
                                listPoints.add(geoPoint);


                            }
                            GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(listPoints);
                            listPolignos.add(geoJsonPolygon);
                        }
                    }
                }

            }

            retorned = new GeoJsonMultiPolygon(listPolignos);
            retorned.getType();

        }

        if (retorned.getType().equals(type)) {
            return retorned;
        } else {
            return null;
        }
    }

}
