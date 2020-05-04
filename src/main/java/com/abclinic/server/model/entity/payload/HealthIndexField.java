package com.abclinic.server.model.entity.payload;

import com.abclinic.server.common.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity.payload
 * @created 5/4/2020 2:46 PM
 */
@Entity
@Table(name = "HealthIndexField")
public class HealthIndexField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = HealthIndex.class)
    @JoinColumn(name = "index_id")
    @JsonView(Views.Abridged.class)
    private HealthIndex healthIndex;

    private String name;

    public HealthIndexField() {
    }

    public HealthIndexField(HealthIndex healthIndex, String name) {
        this.healthIndex = healthIndex;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HealthIndex getHealthIndex() {
        return healthIndex;
    }

    public void setHealthIndex(HealthIndex healthIndex) {
        this.healthIndex = healthIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
