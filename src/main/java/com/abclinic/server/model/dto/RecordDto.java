package com.abclinic.server.model.dto;

import com.abclinic.server.model.entity.payload.record.Record;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 1/9/2020 3:41 PM
 */
public class RecordDto<T extends Record> {
    private int type;
    private T record;

    public RecordDto(int type, T record) {
        this.type = type;
        this.record = record;
    }

    public int getType() {
        return type;
    }

    public T getRecord() {
        return record;
    }
}
