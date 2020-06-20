package br.com.zecode.backendchallenge.config;

import br.com.zecode.backendchallenge.model.BusinessPartner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;

import javax.annotation.PostConstruct;

@Configuration
@DependsOn("mongoTemplate")
public class CollectionsConfig {

    @Autowired
    private MongoOperations mongoOps;

    @PostConstruct
    public void initIndexes() {
        mongoOps.indexOps(BusinessPartner.class)
                .ensureIndex(
                        new Index().on("document", Sort.Direction.ASC).unique()
                );

        mongoOps.indexOps(BusinessPartner.class)
                .ensureIndex(
                        new GeospatialIndex("address").typed(GeoSpatialIndexType.GEO_2DSPHERE)
                );
    }
}