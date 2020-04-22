package com.abclinic.server.common;

/**
 * @author tmduc
 * @package com.abclinic.server.common
 * @created 4/22/2020 10:27 AM
 */
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;

    public SearchCriteria() {

    }

    public SearchCriteria(String key, String operation, Object value) {
        this.key = key.toLowerCase();
        this.operation = operation;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
