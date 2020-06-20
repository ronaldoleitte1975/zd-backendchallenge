package br.com.zecode.backendchallenge.repository.impl;

import br.com.zecode.backendchallenge.model.BusinessPartner;
import br.com.zecode.backendchallenge.repository.CustomBusinessPartnerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;


public class CustomBusinessPartnerRepositoryImpl implements CustomBusinessPartnerRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<BusinessPartner> searchNearestAndInCoverageAreaByPoint(final GeoJsonPoint point) {
        Criteria criteria = Criteria.where("address").near(point).and("coverageArea").intersects(point);
        Query query = new Query(criteria);
        //mongoTemplate.indexOps(GeoJsonPoint.class).ensureIndex(new GeospatialIndex("address"));
        return Optional.ofNullable(mongoTemplate.findOne(query, BusinessPartner.class));
    }


}
