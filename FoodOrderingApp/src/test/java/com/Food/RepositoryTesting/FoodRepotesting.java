package com.Food.RepositoryTesting;

import com.Food.Repository.FoodRepository;
import com.Food.projections.FoodSearchProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FoodRepotesting {
    @Autowired
    public FoodRepository foodRepository;


    @Test
    public void FoodSearchtest(){

        List<FoodSearchProjection> mangoLassi = foodRepository.searchFood("Mango");


    }
}
