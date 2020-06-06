package com.abclinic.server.model.dto.request.put;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 6/6/2020 2:42 PM
 */
public class RequestUpdatePatientDiseaseDto {
    @JsonProperty("diseases")
    private List<Integer> diseaseIds;

    public List<Integer> getDiseaseIds() {
        return diseaseIds;
    }

    public void setDiseaseIds(List<Integer> diseaseIds) {
        this.diseaseIds = diseaseIds;
    }
}
