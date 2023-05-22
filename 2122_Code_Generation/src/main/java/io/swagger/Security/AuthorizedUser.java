package io.swagger.Security;

import io.swagger.model.Enums.UserRole;
import io.swagger.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AuthorizedUser implements UserDetails {

  ArrayList<GrantedAuthority> authorities = null;
  private User user;

  public AuthorizedUser(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    grantedAuthorities.add(new SimpleGrantedAuthority(user.getRoles().name()));
    return grantedAuthorities;
  }

  public void setAuthorities(ArrayList<GrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public String getUsername() {
    return user.getUserName();
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  public String getEmail() {
    return user.getUserName();
  }

  public UUID getId() {
    return user.getId();
  }

  public UserRole getRole() {
    return user.getRoles();
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

}
