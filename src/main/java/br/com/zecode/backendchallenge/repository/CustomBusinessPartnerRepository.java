package br.com.zecode.backendchallenge.repository;

import br.com.zecode.backendchallenge.model.BusinessPartner;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CustomBusinessPartnerRepository {
    public Optional<BusinessPartner> searchNearestAndInCoverageAreaByPoint(final GeoJsonPoint point);
}

