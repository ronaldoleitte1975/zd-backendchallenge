package br.com.zecode.backendchallenge.gateway;

import br.com.zecode.backendchallenge.model.BusinessPartner;

public interface BusinessPartnerService {

    public BusinessPartner getById(String id);

    public BusinessPartner create(BusinessPartner businessPartner);

    public BusinessPartner search(Double lng, Double lat);

    public int count();
}
