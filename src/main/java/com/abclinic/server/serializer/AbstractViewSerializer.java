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
 * @created 5/29/2020 3:05 PM
 */
public abstract class AbstractViewSerializer<T> extends JsonSerializer<T> {
    private Class c;

    public AbstractViewSerializer(Class c) {
        this.c = c;
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.registerModule(new JavaTimeModule());
        mapper.setConfig(mapper.getSerializationConfig().withView(c));

        jsonGenerator.setCodec(mapper);
        jsonGenerator.writeObject(o);
    }
}
