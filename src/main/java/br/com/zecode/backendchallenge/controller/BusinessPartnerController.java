package br.com.zecode.backendchallenge.controller;

import br.com.zecode.backendchallenge.gateway.BusinessPartnerService;
import br.com.zecode.backendchallenge.model.BusinessPartner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/pdvs")
@Validated
public class BusinessPartnerController {

    private BusinessPartnerService bpGateway;

    @Autowired
    public BusinessPartnerController(final BusinessPartnerService bpGateway) {
        this.bpGateway = bpGateway;
    }

    @GetMapping("/{id}")
    @Validated
    public BusinessPartner findById(@PathVariable @NotBlank String id) {
        return bpGateway.getById(id);
    }

    @PostMapping
    @Validated
    public  ResponseEntity<Void> create(@RequestBody @NotNull @Valid BusinessPartner businessPartner) {
        return  ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(bpGateway.create(businessPartner).getId()).toUri()).build();
    }

    @GetMapping(params = {"lat", "lng"})
    public BusinessPartner searchByLatLng(@RequestParam @NotNull @Min(-180) @Max(180) Double lat,
                                          @RequestParam @NotNull @Min(-90) @Max(90) Double lng) {
        return bpGateway.search(lng, lat);
    }


}
