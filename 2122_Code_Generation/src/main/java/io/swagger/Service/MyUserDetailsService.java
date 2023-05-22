package io.swagger.Service;

import io.swagger.Repository.UserRepository;
import io.swagger.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Custom details service class
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Finds a user by email
     * @param email
     * @return Authorized user
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         Optional<User> user = userRepository.findByUserName(email);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("email %s not found", email));
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(user.get().getPassword())
                .roles(user.get().getRoles().toString())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
  }
}