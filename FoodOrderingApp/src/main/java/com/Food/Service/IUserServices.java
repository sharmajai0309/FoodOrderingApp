package com.Food.Service;

import com.Food.Model.User;

import java.util.List;

public interface IUserServices {
	
	public User findByUsername(String username);
	
	public User findByEmail(String email);

	public List<User> findUsers();

}
