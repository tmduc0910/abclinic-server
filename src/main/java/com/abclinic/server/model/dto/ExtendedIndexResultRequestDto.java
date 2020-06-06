package com.abclinic.server.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 6/6/2020 9:56 AM
 */
public class ExtendedIndexResultRequestDto {
    @JsonProperty("index_id")
    private long indexId;
    @JsonProperty("values")
    private List<IndexResultRequestDto> requestDtos;

    public long getIndexId() {
        return indexId;
    }

    public void setIndexId(long indexId) {
        this.indexId = indexId;
    }

    public List<IndexResultRequestDto> getRequestDtos() {
        return requestDtos;
    }

    public void setRequestDtos(List<IndexResultRequestDto> requestDtos) {
        this.requestDtos = requestDtos;
    }
}
