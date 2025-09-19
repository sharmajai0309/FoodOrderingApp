package com.Food.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Food.Model.User;


@Repository
public interface IUserRepository extends JpaRepository<User,Long> {

	// finding user email by Username
	public User findByEmail(String Email);
	
	// find By Username
	Optional<User> findByUsername(String username);

}
