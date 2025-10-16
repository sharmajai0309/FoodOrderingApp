package com.Food.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Food.Model.User;


@Repository
public interface IUserRepository extends JpaRepository<User,Long> {

	// finding user email by Username
	public User findByEmail(String Email);
	
	// find By Username
	Optional<User> findByUsername(String username);

	@Query("SELECT DISTINCT u FROM User u " +
			"LEFT JOIN FETCH u.favorite f " +
			"LEFT JOIN FETCH f.images")
	List<User> findAllWithFavoritesAndImages();


	// Stored Procedures for toggle favorite restaurant
	@Procedure(procedureName = "toggle_favorite")
	void toggleFavorite(@Param("p_user_id") Long userId,
						@Param("p_restaurant_id") Long restaurantId);

}
