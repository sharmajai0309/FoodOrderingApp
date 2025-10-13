package com.Food.Controller;

import com.Food.dto.RestaurantDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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


    @GetMapping("/GetAll")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findUsers();

        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    UserDto dto = new UserDto();

                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());


                    // ✅ Favorites mapping
                    dto.setFavorite(user.getFavorite().stream()
                            .map(restaurant -> {
                                RestaurantDto restaurantDto = new RestaurantDto();
                                restaurantDto.setId(restaurant.getId());
                                restaurantDto.setTitle(restaurant.getName());
                                restaurantDto.setDescription(restaurant.getDescription());
                                restaurantDto.setImages(restaurant.getImages());
                                return restaurantDto;
                            })
                            .collect(Collectors.toSet())
                    );

                    // ✅ Empty arrays
                    dto.setOrders(new ArrayList<>());
                    dto.setAddresses(new ArrayList<>());

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }
}
