package com.abclinic.server.config.security;

import com.abclinic.server.exception.WrongCredentialException;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.entity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author tmduc
 * @package com.abclinic.server.config.security
 * @created 6/2/2020 2:53 PM
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user = userService.findByUsername(username).orElseThrow(WrongCredentialException::new);

        if (passwordEncoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username, password,
                    Collections.singleton(new SimpleGrantedAuthority("USER")));
        } else if (password.equals(user.getPassword())) {
            password = passwordEncoder.encode(password);
            user.setPassword(password);
            userService.save(user);
            return new UsernamePasswordAuthenticationToken(username, password,
                    Collections.singleton(new SimpleGrantedAuthority("USER")));
        } else throw new WrongCredentialException();
    }
}