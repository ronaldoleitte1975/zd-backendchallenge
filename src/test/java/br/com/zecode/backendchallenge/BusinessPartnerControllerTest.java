package br.com.zecode.backendchallenge;

import br.com.zecode.backendchallenge.exception.ErrorDTO;
import br.com.zecode.backendchallenge.model.BusinessPartner;
import com.mongodb.client.model.geojson.GeoJsonObjectType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusinessPartnerControllerTest extends IntegrationTest {

    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldGetById() throws Exception {

        String document = FAKER.number().digits(14);
        String ownerName = FAKER.name().fullName();
        String tradingName = FAKER.company().name();

        GeoJsonPoint address = new GeoJsonPoint(getRandomLng(), getRandomLat());

        Point point1 = new Point(getRandomLng(), getRandomLat());
        Point point2 = new Point(getRandomLng(), getRandomLat());
        Point point3 = new Point(getRandomLng(), getRandomLat());
        Point point4 = new Point(getRandomLng(), getRandomLat());

        GeoJsonPolygon points = new GeoJsonPolygon(Arrays.asList(point1, point2, point3, point4));
        GeoJsonMultiPolygon coverageArea = new GeoJsonMultiPolygon(Collections.singletonList(points));

        BusinessPartner bp = BusinessPartner
                .builder()
                .tradingName(tradingName)
                .ownerName(ownerName)
                .document(document)
                .coverageArea(coverageArea)
                .address(address)
                .build();

        partnerRepository.save(bp);

        mockMvc.perform(get("/pdvs/" + bp.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bp.getId())))
                .andExpect(jsonPath("$.document", is(bp.getDocument())))
                .andExpect(jsonPath("$.ownerName", is(bp.getOwnerName())))
                .andExpect(jsonPath("$.tradingName", is(bp.getTradingName())))
                .andExpect(jsonPath("$.coverageArea.type", is(GeoJsonObjectType.MULTI_POLYGON.getTypeName())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][0][0]", is(point1.getX())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][0][1]", is(point1.getY())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][1][0]", is(point2.getX())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][1][1]", is(point2.getY())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][2][0]", is(point3.getX())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][2][1]", is(point3.getY())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][3][0]", is(point4.getX())))
                .andExpect(jsonPath("$.coverageArea.coordinates[0][0][3][1]", is(point4.getY())))
                .andExpect(jsonPath("$.address.type", is(GeoJsonObjectType.POINT.getTypeName())))
                .andExpect(jsonPath("$.address.coordinates[0]", is(bp.getAddress().getX())))
                .andExpect(jsonPath("$.address.coordinates[1]", is(bp.getAddress().getY())));
    }

    @Test
    public void shouldNotGetIdNotFound() throws Exception {
        mockMvc.perform(get("/pdvs/5555555555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldGetPointInCoverageArea() throws Exception {

        String document = FAKER.number().digits(14);
        String ownerName = FAKER.name().fullName();
        String tradingName = FAKER.company().name();

        Point point1 = new Point(-46.55169, -23.62617);
        Point point2 = new Point(-46.55327, -23.62746);
        Point point3 = new Point(-46.55063, -23.62812);
        Point point4 = new Point(-46.54987, -23.62686);
        Point point5 = new Point(-46.55169, -23.62617);

        Point point11 = new Point(-46.55149, -23.62691);
        Point point21 = new Point(-46.55104, -23.62755);
        Point point31 = new Point(-46.55079, -23.6274);
        Point point41 = new Point(-46.55149, -23.62691);

        GeoJsonPoint address = new GeoJsonPoint(-46.55111, -23.62671);

        GeoJsonPolygon borderOutPoints = new GeoJsonPolygon(Arrays.asList(point1, point2, point3, point4, point5));
        GeoJsonPolygon borderInPoints = new GeoJsonPolygon(Arrays.asList(point11, point21, point31, point41));
        GeoJsonMultiPolygon coverageArea = new GeoJsonMultiPolygon(Arrays.asList(borderOutPoints, borderInPoints));

        BusinessPartner bp = BusinessPartner.builder()
                .tradingName(tradingName)
                .ownerName(ownerName)
                .address(address)
                .coverageArea(coverageArea)
                .address(address).build();

        partnerRepository.save(bp);

        Point pointInCoverageArea = new Point(-46.55163, -23.62732);

        this.mockMvc.perform(get("/pdvs")
                .queryParam("lat", String.valueOf(pointInCoverageArea.getY()))
                .queryParam("lng", String.valueOf(pointInCoverageArea.getX()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bp.getId())));

        partnerRepository.deleteById(bp.getId());
    }

    @Test
    public void shouldNotGetPointLatLngOutCoverageArea() throws Exception {
        String document = FAKER.number().digits(14);
        String ownerName = FAKER.name().fullName();
        String tradingName = FAKER.company().name();

        Point point1 = new Point(-46.55169, -23.62617);
        Point point2 = new Point(-46.55327, -23.62746);
        Point point3 = new Point(-46.55063, -23.62812);
        Point point4= new Point(-46.54987, -23.62686);
        Point point5 = new Point(-46.55169, -23.62617);

        Point point11 = new Point(-46.55149, -23.62691);
        Point point21 = new Point(-46.55104, -23.62755);
        Point point31 = new Point(-46.55079, -23.6274);
        Point point41 = new Point(-46.55149, -23.62691);

        GeoJsonPoint address = new GeoJsonPoint(-46.55111, -23.62671);

        GeoJsonPolygon borderPoints = new GeoJsonPolygon(Arrays.asList(point1, point2, point3, point4, point5));
        GeoJsonPolygon borderInPoints = new GeoJsonPolygon(Arrays.asList(point11, point21, point31, point41));
        GeoJsonMultiPolygon coverageArea = new GeoJsonMultiPolygon(Arrays.asList(borderPoints, borderInPoints));

        BusinessPartner bp = BusinessPartner.builder()
                .tradingName(tradingName)
                .ownerName(ownerName)
                .document(document)
                .coverageArea(coverageArea)
                .address(address).build();

        partnerRepository.save(bp);

        Point pointCoverage = new Point(-49.55075, -27.62613);

        this.mockMvc.perform(get("/pdvs")
                .queryParam("lat", String.valueOf(pointCoverage.getY()))
                .queryParam("lng", String.valueOf(pointCoverage.getX()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        partnerRepository.deleteById(bp.getId());
    }

    @Test
    public void shouldGetNearestBetweenTwoPartnerInCoverageArea() throws Exception {

        String documentPartner1 = FAKER.number().digits(14);
        String ownerNamePartner1 = FAKER.name().fullName();
        String tradingNamePartner1 = FAKER.company().name();
        GeoJsonPoint addressPartner1 = new GeoJsonPoint(-47.55111, -24.62671);

        String documentPartner2 = FAKER.number().digits(14);
        String ownerNamePartner2 = FAKER.name().fullName();
        String tradingNamePartner2 = FAKER.company().name();
        GeoJsonPoint addressPartner2 = new GeoJsonPoint(-46.55111, -23.62671);

        Point point1 = new Point(-46.55169, -23.62617);
        Point point2 = new Point(-46.55327, -23.62746);
        Point point3 = new Point(-46.55063, -23.62812);
        Point point4 = new Point(-46.54987, -23.62686);
        Point point5 = new Point(-46.55169, -23.62617);

        Point point11 = new Point(-46.55149, -23.62691);
        Point point21 = new Point(-46.55104, -23.62755);
        Point point31 = new Point(-46.55079, -23.6274);
        Point point41 = new Point(-46.55149, -23.62691);

        GeoJsonPolygon borderOutPoints = new GeoJsonPolygon(Arrays.asList(point1, point2, point3, point4, point5));
        GeoJsonPolygon borderInPoints = new GeoJsonPolygon(Arrays.asList(point11, point21, point31, point41));
        GeoJsonMultiPolygon coverageArea = new GeoJsonMultiPolygon(Arrays.asList(borderOutPoints, borderInPoints));

        BusinessPartner bp1 = BusinessPartner.builder()
                .tradingName(tradingNamePartner1)
                .ownerName(ownerNamePartner1)
                .document(documentPartner1)
                .coverageArea(coverageArea)
                .address(addressPartner1).build();

        partnerRepository.save(bp1);

        BusinessPartner bp2 = BusinessPartner.builder()
                .tradingName(tradingNamePartner2)
                .ownerName(ownerNamePartner2)
                .document(documentPartner2)
                .coverageArea(coverageArea)
                .address(addressPartner2)
                .build();

        partnerRepository.save(bp2);

        Point pointInCoverageNearPartnerB = new Point(-46.55163, -23.62732);

        this.mockMvc.perform(get("/pdvs")
                .queryParam("lat", String.valueOf(pointInCoverageNearPartnerB.getY()))
                .queryParam("lng", String.valueOf(pointInCoverageNearPartnerB.getX()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bp2.getId())));

        partnerRepository.deleteById(bp1.getId());
        partnerRepository.deleteById(bp2.getId());
    }

    @Test
    public void shouldCreatePartner() throws Exception {

        String tradingName = FAKER.company().name();
        String ownerName = FAKER.name().fullName();
        String document = "21.687.442/0001-28";

        List<Point> pointList1 = new ArrayList<>(10);
        IntStream.range(0, 10).forEach(i -> pointList1.add(new Point(getRandomLng(), getRandomLat())));

        List<Point> pointList2 = new ArrayList<>(5);
        IntStream.range(0, 5).forEach(i -> pointList2.add(new Point(getRandomLng(), getRandomLat())));

        GeoJsonPoint address = new GeoJsonPoint(getRandomLng(), getRandomLat());

        GeoJsonPolygon points = new GeoJsonPolygon(pointList1);
        GeoJsonPolygon points2 = new GeoJsonPolygon(pointList2);
        GeoJsonMultiPolygon coverageArea = new GeoJsonMultiPolygon(Arrays.asList(points, points2));

        BusinessPartner bp = BusinessPartner
                .builder()
                .tradingName(tradingName)
                .ownerName(ownerName)
                .document(document)
                .coverageArea(coverageArea)
                .address(address).build();

        String contentAsString = this.mockMvc.perform(post("/pdvs")
                .content(objectMapper.writeValueAsString(bp))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();


        assertNotNull(contentAsString.equals(""));
    }

    @Test
    public void shouldNotCreateCNPJAlreadyExists() throws Exception {

        String tradingName = FAKER.company().name();
        String ownerName = FAKER.name().fullName();
        String document = "95.616.272/0001-27";

        GeoJsonPoint address = new GeoJsonPoint(getRandomLng(), getRandomLat());

        GeoJsonPolygon points = new GeoJsonPolygon(Arrays.asList(new Point(getRandomLng(), getRandomLat()), new Point(getRandomLng(), getRandomLat())));
        GeoJsonMultiPolygon coverageArea = new GeoJsonMultiPolygon(Collections.singletonList(points));

        BusinessPartner bp = BusinessPartner.builder().
                tradingName(tradingName)
                .ownerName(ownerName)
                .document(document)
                .coverageArea(coverageArea)
                .address(address).build();

        partnerRepository.save(bp);

        String tradingNameDTO = FAKER.company().name();
        String ownerNameDTO = FAKER.name().fullName();

        GeoJsonPoint addressDTO = new GeoJsonPoint(getRandomLng(), getRandomLat());

        GeoJsonPolygon pointsDTO = new GeoJsonPolygon(Arrays.asList(new Point(getRandomLng(), getRandomLat()), new Point(getRandomLng(), getRandomLat())));
        GeoJsonMultiPolygon coverageAreaDTO = new GeoJsonMultiPolygon(Collections.singletonList(pointsDTO));

        BusinessPartner bpDTO = BusinessPartner.builder()
                .tradingName(tradingNameDTO)
                .ownerName(ownerNameDTO)
                .document(document)
                .coverageArea(coverageAreaDTO)
                .address(addressDTO).build();

        String contentAsString = this.mockMvc.perform(post("/pdvs")
                .content(objectMapper.writeValueAsString(bpDTO))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ErrorDTO[] errorDTOS = objectMapper.readValue(contentAsString, ErrorDTO[].class);

        Optional<ErrorDTO> optionalErrorDTO = Arrays.stream(errorDTOS).findFirst();
        assertTrue(optionalErrorDTO.isPresent());

        ErrorDTO errorDTO = optionalErrorDTO.get();
        assertTrue(errorDTO.isKnown());
        assertEquals("business partner[" + document + "].duplicate", errorDTO.getMessage());
    }
}
