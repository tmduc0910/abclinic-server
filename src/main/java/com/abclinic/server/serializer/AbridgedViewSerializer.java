package com.abclinic.server.serializer;

import com.abclinic.server.common.base.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * @author tmduc
 * @package com.abclinic.server.serializer
 * @created 4/11/2020 10:05 AM
 */
public class AbridgedViewSerializer extends AbstractViewSerializer<Object> {
    public AbridgedViewSerializer() {
        super(Views.Abridged.class);
    }
}
