package com.classspace_backend.demo.security;



import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    
	private final Long userId;
    private final String email;
    private final String password;
    private final String roleName; // STUDENT/TEACHER/COORDINATOR

    public CustomUserDetails(Long userId, String email, String password, String roleName) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.roleName = roleName;
    }

    public Long getUserId() { return userId; }
    public String getRoleName() { return roleName; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring expects ROLE_*
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

