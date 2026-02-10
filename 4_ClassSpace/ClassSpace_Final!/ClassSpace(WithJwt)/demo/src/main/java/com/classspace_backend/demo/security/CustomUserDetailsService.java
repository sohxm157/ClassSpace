package com.classspace_backend.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String roleName = (u.getRole() != null && u.getRole().getRoleName() != null)
                ? u.getRole().getRoleName()
                : "STUDENT";

        return new CustomUserDetails(u.getUserId(), u.getEmail(), u.getPassword(), roleName);
    }
}

