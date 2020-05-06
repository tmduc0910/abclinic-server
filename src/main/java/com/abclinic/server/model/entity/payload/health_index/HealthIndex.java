package com.abclinic.server.model.entity.payload.health_index;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY, targetEntity = HealthIndexField.class, mappedBy = "healthIndex")
    @JsonView(Views.Abridged.class)
    private List<HealthIndexField> fields;

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

    public List<HealthIndexField> getFields() {
        return fields;
    }

    public void setFields(List<HealthIndexField> fields) {
        this.fields = fields;
    }
}
