package com.example.users_service.configs;

import com.example.users_service.entities.UserEntity;
import com.example.users_service.repositories.UsersRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UsersRepository userRepository;

    public UserAuthenticationProvider(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken upAuth = (UsernamePasswordAuthenticationToken) authentication;
        String login = (String) authentication.getPrincipal();

        String password = (String) upAuth.getCredentials();

        String storedPassword = userRepository.findUserEntityByLogin(login).map(UserEntity::getPassword)
                .orElseThrow(() -> new BadCredentialsException("Illegal id or passowrd"));

        if (Objects.equals(password, "") || !Objects.equals(password, storedPassword)) {
            throw new BadCredentialsException("Illegal id or passowrd");
        }

        Object principal = authentication.getPrincipal();
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                principal, authentication.getCredentials(),
                Collections.emptyList());
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
