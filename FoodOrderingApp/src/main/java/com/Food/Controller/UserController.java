package com.Food.Controller;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.Food.Model.USER_ROLE;
import com.Food.Model.User;
import com.Food.Service.IUserServices;
import com.Food.dto.UserDto;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private IUserServices userService;
    
    @Autowired
    private ModelMapper modelMapper;

@GetMapping("/profile")
@PreAuthorize("hasRole('CUSTOMER')")
public ResponseEntity<UserDto> finduserbytoken() {
    // This gets the authentication object from the security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // authentication.getName() returns the userName from the token
    String username = authentication.getName();
    log.info("name fetched form token" + username);
    User user = userService.findByUsername(username);
    UserDto userDto = new UserDto();
    modelMapper.map(user, userDto);

    log.info("User fetched form token" + username);
    return ResponseEntity.ok(userDto);
}


//    @GetMapping("/GetAll")
//    @PreAuthorize("permitAll")
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        List<User> users = userService.findUsers();
//
//        List<UserDto> userDtos = users.stream()
//                .map(user -> {
//                    UserDto userDto = new UserDto();
//                    userDto.setUsername(user.getUsername());
//                    userDto.setEmail(user.getEmail());
//                    return userDto;
//                })
//                .collect(Collectors.toList());
//
//
//
//
//
//        log.info("Data fetched and converted");
//
//        return ResponseEntity.ok(userDtos);
//    }

}
