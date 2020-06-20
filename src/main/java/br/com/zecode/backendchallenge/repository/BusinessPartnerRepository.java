package br.com.zecode.backendchallenge.repository;

import br.com.zecode.backendchallenge.model.BusinessPartner;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusinessPartnerRepository extends MongoRepository<BusinessPartner, String>, CustomBusinessPartnerRepository {

    public Boolean existsByDocument(String document);
}
