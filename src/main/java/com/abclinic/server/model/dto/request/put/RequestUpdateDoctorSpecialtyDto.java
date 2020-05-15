package com.abclinic.server.model.dto.request.put;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 5/15/2020 9:41 AM
 */
public class RequestUpdateDoctorSpecialtyDto {
    @JsonProperty("specialties")
    private long[] specialtyIds;

    public long[] getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(long[] specialtyIds) {
        this.specialtyIds = specialtyIds;
    }
}
