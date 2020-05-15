package com.abclinic.server.model.dto.request.post;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request
 * @created 5/14/2020 8:52 PM
 */
public class RequestCreateHealthIndexDto {
    private String name;
    private String description;
    private String[] fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }
}
