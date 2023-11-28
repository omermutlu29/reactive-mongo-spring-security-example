package com.chance.auth.entity;

import com.chance.auth.enums.EnumRole;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Getter
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private final String id;

    private String username;
    private String password;
    private String email;
    private String msisdn;
    private Boolean isAccountNonLocked;
    private Boolean isAccountNonExpired;
    private Boolean isCredentialsNonExpired;
    private Boolean isEnabled;
    private List<EnumRole> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    public List<String> getRolesAsListString() {
        return this.roles.stream().map(Enum::name).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
