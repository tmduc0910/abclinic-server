package com.abclinic.server.model.dto.request.put;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 5/14/2020 8:55 PM
 */
public class RequestUpdateDiseaseDto {
    private String name;
    private String description;

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
}
