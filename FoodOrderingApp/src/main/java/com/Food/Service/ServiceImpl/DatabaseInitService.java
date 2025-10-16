package com.Food.Service.ServiceImpl;

// Adjust package as per your structure

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class DatabaseInitService {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        try {
            String procedureSql = """
                CREATE PROCEDURE IF NOT EXISTS toggle_favorite(
                    IN p_user_id BIGINT,
                    IN p_restaurant_id BIGINT
                )
                BEGIN
                    DECLARE favorite_exists INT DEFAULT 0;
                    
                    SELECT COUNT(*) INTO favorite_exists 
                    FROM user_favorite 
                    WHERE user_id = p_user_id AND favorite_id = p_restaurant_id;
                    
                    IF favorite_exists > 0 THEN
                        DELETE FROM user_favorite 
                        WHERE user_id = p_user_id AND favorite_id = p_restaurant_id;
                    ELSE
                        INSERT INTO user_favorite (user_id, favorite_id) 
                        VALUES (p_user_id, p_restaurant_id);
                    END IF;
                END
                """;

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(procedureSql);
                System.out.println("✅ Stored procedure created/verified");
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Stored procedure already exists: " + e.getMessage());
        }
    }
}
