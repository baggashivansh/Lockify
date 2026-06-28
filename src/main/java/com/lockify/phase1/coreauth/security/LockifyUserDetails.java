package com.lockify.phase1.coreauth.security;

import com.lockify.shared.domain.entity.Permission;
import com.lockify.shared.domain.entity.Role;
import com.lockify.shared.domain.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security UserDetails implementation.
 *
 * Authorities me roles (ROLE_USER) aur permissions (READ, CREATE) dono include hote hain.
 * @PreAuthorize("hasAuthority('CREATE')") isi se kaam karta hai.
 */
@Getter
public class LockifyUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final boolean enabled;
    private final boolean accountLocked;
    private final Set<GrantedAuthority> authorities;

    public LockifyUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.enabled = user.isEnabled();
        this.accountLocked = user.isAccountLocked();
        this.authorities = buildAuthorities(user.getRoles());
    }

    /**
     * JWT se bhi UserDetails bana sakte hain - har request pe DB hit avoid karne ke liye.
     */
    public LockifyUserDetails(Long userId, String username, Set<String> roles, Set<String> permissions) {
        this.id = userId;
        this.username = username;
        this.email = null;
        this.passwordHash = null;
        this.enabled = true;
        this.accountLocked = false;
        this.authorities = buildAuthoritiesFromStrings(roles, permissions);
    }

    private Set<GrantedAuthority> buildAuthorities(Set<Role> roles) {
        Set<GrantedAuthority> auth = new HashSet<>();
        for (Role role : roles) {
            auth.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
            for (Permission permission : role.getPermissions()) {
                auth.add(new SimpleGrantedAuthority(permission.getName().name()));
            }
        }
        return auth;
    }

    private Set<GrantedAuthority> buildAuthoritiesFromStrings(Set<String> roles, Set<String> permissions) {
        Set<GrantedAuthority> auth = new HashSet<>();
        roles.forEach(r -> auth.add(new SimpleGrantedAuthority("ROLE_" + r)));
        permissions.forEach(p -> auth.add(new SimpleGrantedAuthority(p)));
        return auth;
    }

    public Set<String> getRoleNames() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
