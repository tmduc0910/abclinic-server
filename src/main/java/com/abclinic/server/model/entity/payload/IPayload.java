package com.abclinic.server.model.entity.payload;

import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.entity.payload
 * @created 5/5/2020 3:44 PM
 */
public interface IPayload {
    long getId();
    LocalDateTime getCreatedAt();
}