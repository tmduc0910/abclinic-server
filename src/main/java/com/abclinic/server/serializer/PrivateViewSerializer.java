package com.abclinic.server.serializer;

import com.abclinic.server.common.base.Views;

/**
 * @author tmduc
 * @package com.abclinic.server.serializer
 * @created 5/29/2020 3:21 PM
 */
public class PrivateViewSerializer extends AbstractViewSerializer<Object> {
    public PrivateViewSerializer() {
        super(Views.Private.class);
    }
}
