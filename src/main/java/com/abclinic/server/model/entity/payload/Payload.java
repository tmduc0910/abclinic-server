package com.abclinic.server.model.entity.payload;

import com.abclinic.server.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity.payload
 * @created 2/10/2020 4:05 PM
 */
@MappedSuperclass
public abstract class Payload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Abridged.class)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
