package com.abclinic.server.model.dto;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.serializer.ViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/28/2020 9:29 AM
 */
public class GetIndexResultResponseDto {
    private long tagId;

    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = ViewSerializer.class)
    private HealthIndexSchedule schedule;

    private List<ResultDto> resultDtos;

    public GetIndexResultResponseDto(long tagId, HealthIndexSchedule schedule, List<ResultDto> resultDtos) {
        this.tagId = tagId;
        this.schedule = schedule;
        this.resultDtos = resultDtos;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public HealthIndexSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(HealthIndexSchedule schedule) {
        this.schedule = schedule;
    }

    public List<ResultDto> getResultDtos() {
        return resultDtos;
    }

    public void setResultDtos(List<ResultDto> resultDtos) {
        this.resultDtos = resultDtos;
    }
}
