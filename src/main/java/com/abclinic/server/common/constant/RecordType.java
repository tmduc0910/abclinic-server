package com.abclinic.server.common.constant;

import java.util.Arrays;

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

    public static RecordType getType(int type) {
        return Arrays.stream(RecordType.values()).filter(t -> t.getValue() == type).findFirst().get();
    }
}