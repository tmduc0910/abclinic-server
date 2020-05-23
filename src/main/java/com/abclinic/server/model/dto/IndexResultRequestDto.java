package com.abclinic.server.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/10/2020 2:58 PM
 */
public class IndexResultRequestDto {
    @JsonProperty("field_id")
    private long fieldId;
    private String value;

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
