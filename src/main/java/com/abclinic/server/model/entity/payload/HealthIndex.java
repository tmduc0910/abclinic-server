package com.abclinic.server.model.entity.payload;

import com.abclinic.server.common.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity
 * @created 12/30/2019 3:07 PM
 */

@Entity
@Table(name = "health_index")
public class HealthIndex extends IPayloadIpml {

    @JsonView(Views.Abridged.class)
    private String name;

    @JsonView(Views.Public.class)
    private String description;

    public HealthIndex() {
    }

    public HealthIndex(String name, String description) {
        this.name = name;
        this.description = description;
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
