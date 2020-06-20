package br.com.zecode.backendchallenge.config;

import br.com.zecode.backendchallenge.gateway.BusinessPartnerService;
import br.com.zecode.backendchallenge.model.BusinessPartner;
import br.com.zecode.backendchallenge.model.deserializer.GeoJsonMultiPolygonDeserializer;
import br.com.zecode.backendchallenge.model.deserializer.GeoJsonPointDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
@org.springframework.context.annotation.Profile("default")
public class ApplicationDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private Logger logger = LoggerFactory.getLogger(ApplicationDataInitializer.class);

    private final BusinessPartnerService businessPartnerService;

    @Autowired
    ResourceLoader resourceLoader;

    public ApplicationDataInitializer(BusinessPartnerService businessPartnerService) {
        this.businessPartnerService = businessPartnerService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (businessPartnerService.count() == 0)
            loadDataFromFile().forEach(businessPartnerService::create);
    }

    private List<BusinessPartner> loadDataFromFile() {
        ArrayList<BusinessPartner> list = new ArrayList<>();
        try {

            Resource resource = resourceLoader.getResource("classpath:bps.json");
            InputStream inputStream = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            JsonParser jsonParser = new JsonParser();
            JsonObject jo = (JsonObject) jsonParser.parse(br);
            JsonArray jsonArray = jo.getAsJsonArray("pdvs");


            for (final JsonElement jsonElement : jsonArray) {
                Gson gson = new Gson();

                ObjectMapper objectMapper = new ObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addDeserializer(GeoJsonMultiPolygon.class, new GeoJsonMultiPolygonDeserializer());
                objectMapper.registerModule(simpleModule);

                String id = ((JsonObject) jsonElement).get("id").getAsString();
                String tradingName = ((JsonObject) jsonElement).get("tradingName").getAsString();
                String ownerName = ((JsonObject) jsonElement).get("ownerName").getAsString();
                String document = ((JsonObject) jsonElement).get("document").getAsString();
                JsonObject coverageArea = ((JsonObject) jsonElement).get("coverageArea").getAsJsonObject();
                GeoJsonMultiPolygon geoJsonMultiPolygon = objectMapper.readValue(coverageArea.toString(), GeoJsonMultiPolygon.class);
                JsonObject address = ((JsonObject) jsonElement).get("address").getAsJsonObject();
                objectMapper = new ObjectMapper();
                simpleModule = new SimpleModule();
                simpleModule.addDeserializer(GeoJsonPoint.class, new GeoJsonPointDeserializer());
                objectMapper.registerModule(simpleModule);
                GeoJsonPoint geoJsonPoint = objectMapper.readValue(address.toString(), GeoJsonPoint.class);

                BusinessPartner entity = BusinessPartner.builder()
                        .id(id)
                        .tradingName(tradingName)
                        .ownerName(ownerName)
                        .document(document)
                        .address(geoJsonPoint)
                        .coverageArea(geoJsonMultiPolygon)
                        .build();

                list.add(entity);
            }

            br.close();
            logger.info("Json file loaded successfuly!");
        } catch (IOException ie) {
            logger.error("Load Json file error:" + ie.getLocalizedMessage());
        }
        return list;
    }
}
