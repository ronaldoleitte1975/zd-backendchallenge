package br.com.zecode.backendchallenge.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class BusinessPartnerNotFoundException extends BaseException
{
    public BusinessPartnerNotFoundException(final String id) {

        super(String.format("business partner[%s].notFound", id));
    }
}
