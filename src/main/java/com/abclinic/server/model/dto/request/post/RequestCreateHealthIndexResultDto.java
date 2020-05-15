package com.abclinic.server.model.dto.request.post;

import com.abclinic.server.model.dto.IndexResultRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/14/2020 9:18 PM
 */
public class RequestCreateHealthIndexResultDto {
    @JsonProperty("schedule_id")
    private long scheduleId;
    private List<IndexResultRequestDto> results;

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public List<IndexResultRequestDto> getResults() {
        return results;
    }

    public void setResults(List<IndexResultRequestDto> results) {
        this.results = results;
    }
}
