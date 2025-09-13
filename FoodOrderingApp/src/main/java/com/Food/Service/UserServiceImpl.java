package com.Food.Service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Food.Model.User;
import com.Food.Repository.IUserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements IUserServices ,UserDetailsService{

    private final IUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    
    private UserServiceImpl(IUserRepository repo,PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User findByUsername(String username) {
        log.info("Entering findByUsername with username: {}", username);
        return repo.findByUsername(username).orElseThrow();
    }

    @Override
    public User findByEmail(String email) {
        log.info("Entering findByEmail with email: {}", email);
        return repo.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Entering loadUserByUsername with username: {}", username);
        User user = findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_"+ user.getRole().name())
        );
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
}