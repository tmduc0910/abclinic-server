package com.abclinic.server.model.dto.request.post;

import com.abclinic.server.model.dto.ExtendedIndexResultRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 6/6/2020 10:09 AM
 */
public class RequestCreateOtherHealthIndexResultDto {
    @JsonProperty("results")
    private List<ExtendedIndexResultRequestDto> requestDtos;

    public List<ExtendedIndexResultRequestDto> getRequestDtos() {
        return requestDtos;
    }

    public void setRequestDtos(List<ExtendedIndexResultRequestDto> requestDtos) {
        this.requestDtos = requestDtos;
    }
}
