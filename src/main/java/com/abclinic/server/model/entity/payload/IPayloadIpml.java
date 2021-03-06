package com.abclinic.server.model.entity.payload;

import com.abclinic.server.common.base.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity.payload
 * @created 2/10/2020 4:05 PM
 */
@MappedSuperclass
public abstract class IPayloadIpml implements IPayload, Serializable {
    private static final long serialVersionUID = 1L;

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
