package com.abclinic.server.constant;

/**
 * @author tmduc
 * @package com.abclinic.server.constant
 * @created 12/30/2019 3:52 PM
 */
public enum RecordType {
    MEDICAL(0),
    DIET(1);

    private int value;

    RecordType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}