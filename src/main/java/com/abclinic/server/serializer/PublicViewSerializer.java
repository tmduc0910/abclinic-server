package com.abclinic.server.serializer;

import com.abclinic.server.common.base.Views;

/**
 * @author tmduc
 * @package com.abclinic.server.serializer
 * @created 5/29/2020 3:08 PM
 */
public class PublicViewSerializer extends AbstractViewSerializer<Object> {
    public PublicViewSerializer() {
        super(Views.Public.class);
    }
}
