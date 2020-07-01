package com.abclinic.server.model.dto.request.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.delete
 * @created 7/1/2020 3:01 PM
 */
public class RequestDeleteDiseaseDto {
    @JsonProperty("diseases")
    private List<Integer> diseaseIds;

    public List<Integer> getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(List<Integer> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }
}
