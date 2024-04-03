package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.Authority;
import CSE4186.interview.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record Oauth2Principal(
        User user,
        Map<String, Object> attributes,
        String attributeKey) implements OAuth2User, UserDetails {

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return attributes.get(attributeKey).toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set authorites = new HashSet<GrantedAuthority>();
        for(Authority auth : user.getAuthoritySet()){
            authorites.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorites;
    }

    @Override
    public String getName() {
        return user.getName();
    }
}