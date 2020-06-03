package com.abclinic.server.model.entity.payload.health_index;

import com.abclinic.server.common.base.Views;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.abclinic.server.serializer.AbridgedViewSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @JsonView(Views.Abridged.class)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = HealthIndexField.class, mappedBy = "healthIndex", cascade = CascadeType.ALL)
    @JsonView(Views.Abridged.class)
    @JsonSerialize(using = AbridgedViewSerializer.class)
    private List<HealthIndexField> fields = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

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

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addField(HealthIndexField field) {
        this.fields.add(field);
        field.setHealthIndex(this);
    }

    public void removeField(HealthIndexField field) {
        fields.remove(field);
        field.setHealthIndex(null);
    }
}
