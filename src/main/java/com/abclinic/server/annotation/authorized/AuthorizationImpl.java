package com.abclinic.server.annotation.authorized;

import com.abclinic.server.model.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author tmduc
 * @package com.abclinic.server.annotation.authorized
 * @created 1/11/2020 2:54 PM
 */
@Component
public class AuthorizationImpl {
    public boolean authorize(User user, Class<? extends User>[] authorizedRoles) {
        return Arrays.asList(authorizedRoles).contains(user.getClass());
    }
}
