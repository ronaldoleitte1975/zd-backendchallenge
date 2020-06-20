package br.com.zecode.backendchallenge.gateway.impl;

import br.com.zecode.backendchallenge.exception.BusinessPartnerDuplicated;
import br.com.zecode.backendchallenge.exception.BusinessPartnerNotFoundException;
import br.com.zecode.backendchallenge.gateway.BusinessPartnerService;
import br.com.zecode.backendchallenge.model.BusinessPartner;
import br.com.zecode.backendchallenge.repository.BusinessPartnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BusinessPartnerServiceImpl implements BusinessPartnerService {

    private BusinessPartnerRepository businessPartnerRepository;

    public BusinessPartnerServiceImpl(BusinessPartnerRepository repository) {
        this.businessPartnerRepository = repository;
    }

    @Override
    public BusinessPartner getById(final String id) {
        return businessPartnerRepository.findById(id)
                .orElseThrow(() -> new BusinessPartnerNotFoundException(id));
    }

    @Override
    public BusinessPartner create(BusinessPartner businessPartner) {
        Boolean exists = businessPartnerRepository.existsByDocument(businessPartner.getDocument());

        if (exists)
            throw new BusinessPartnerDuplicated(businessPartner.getDocument());

        BusinessPartner bp = businessPartnerRepository.save(businessPartner);

        log.info("Business partner has been created: " + businessPartner.toString());

        return bp;

    }

    @Override
    public BusinessPartner search(final Double lng, final Double lat) {
        Point point = new Point(lng, lat);
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(point);
        return businessPartnerRepository.searchNearestAndInCoverageAreaByPoint(geoJsonPoint)
                .orElseThrow(() -> new BusinessPartnerNotFoundException("Business Partner was not found through lat and lng"));

    }

    @Override
    public int count() {
        return businessPartnerRepository.findAll().size();
    }
}
