package com.Food.Service.ServiceImpl;

import java.util.Collections; 
import java.util.List;

import com.Food.Service.IUserServices;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Food.JwtConfig.Jwtutil;
import com.Food.Model.User;
import com.Food.Repository.IUserRepository;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements IUserServices,UserDetailsService{

    private final IUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final Jwtutil jwtutil;

    //Constructor
    public UserServiceImpl(IUserRepository repo,PasswordEncoder passwordEncoder,Jwtutil jwtutil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtutil = jwtutil;
    }
    
    
    
    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.info("Entering findByUsername with username: {}", username);



        
        return repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.info("Entering findByEmail with email: {}", email);
        return repo.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsers() {
        return repo.findAllWithFavoritesAndImages();

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