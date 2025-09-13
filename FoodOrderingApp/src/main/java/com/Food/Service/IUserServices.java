package com.Food.Service;

import com.Food.Model.User;

public interface IUserServices {
	
	public User findByUsername(String username);
	
	public User findByEmail(String email);

}
