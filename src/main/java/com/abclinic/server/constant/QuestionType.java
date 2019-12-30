package com.abclinic.server.constant;

/**
 * @author tmduc
 * @package com.abclinic.server.constant
 * @created 12/30/2019 2:26 PM
 */
public enum QuestionType {
    DIET(0),
    INQUIRY(1);

    private int value;

    QuestionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
