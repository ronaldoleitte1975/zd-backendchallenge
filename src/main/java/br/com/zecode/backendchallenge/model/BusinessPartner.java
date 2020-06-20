package br.com.zecode.backendchallenge.model;

import br.com.zecode.backendchallenge.model.deserializer.GeoJsonMultiPolygonDeserializer;
import br.com.zecode.backendchallenge.model.deserializer.GeoJsonPointDeserializer;
import br.com.zecode.backendchallenge.model.serializer.GeoJsonMultiPolygonSerializer;
import br.com.zecode.backendchallenge.model.serializer.GeoJsonPointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Builder
@Document
public class BusinessPartner implements Serializable {

    private static final long serialVersionUID = 1010225128202819155L;

    @Id
    private String id;

    @NotBlank
    private String tradingName;

    @NotBlank
    private String ownerName;

    @NotBlank
    @CNPJ
    private String document;

    @NotNull
    @JsonSerialize(using = GeoJsonMultiPolygonSerializer.class)
    @JsonDeserialize(using = GeoJsonMultiPolygonDeserializer.class)
    private GeoJsonMultiPolygon coverageArea;

    @NotNull
    @JsonSerialize(using = GeoJsonPointSerializer.class)
    @JsonDeserialize(using = GeoJsonPointDeserializer.class)
    private GeoJsonPoint address;

}
