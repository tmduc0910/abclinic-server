package com.abclinic.server.websocket;

import java.security.Principal;

/**
 * @author tmduc
 * @package com.abclinic.server.websocket
 * @created 2/26/2020 4:08 PM
 */
public class StompPrincipal implements Principal {
    private String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
