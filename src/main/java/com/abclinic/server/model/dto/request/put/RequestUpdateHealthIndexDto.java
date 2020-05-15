package com.abclinic.server.model.dto.request.put;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.put
 * @created 5/14/2020 8:56 PM
 */
public class RequestUpdateHealthIndexDto {
    private long id;
    private String name;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
