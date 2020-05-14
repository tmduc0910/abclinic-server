package com.abclinic.server.model.dto;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/10/2020 2:58 PM
 */
public class IndexResultRequestDto {
    private long fieldId;
    private String value;

    public IndexResultRequestDto(long fieldId, String value) {
        this.fieldId = fieldId;
        this.value = value;
    }

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