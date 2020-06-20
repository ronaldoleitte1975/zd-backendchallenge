package br.com.zecode.backendchallenge.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(BAD_REQUEST)
public class BusinessPartnerDuplicated extends BaseException
{
    public BusinessPartnerDuplicated(final String document) {

        super(String.format("business partner[%s].duplicate", document));
    }
}
