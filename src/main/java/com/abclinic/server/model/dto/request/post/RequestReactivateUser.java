package com.abclinic.server.model.dto.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/21/2020 2:48 PM
 */
public class RequestReactivateUser {
    @JsonProperty("id")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
