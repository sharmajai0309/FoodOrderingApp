package com.Food;

import com.Food.Model.IngredientCategory;
import com.Food.Model.IngredientItem;
import com.Food.Repository.ICategoryRepository;
import com.Food.Repository.IngredientItemRepository;
import com.Food.Service.IngredientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@SpringBootTest
class FoodOrderingAppApplicationTests {

	@Autowired
	private IngredientService ingredientService;

	@Autowired
	private ICategoryRepository repository;

	@Autowired
	IngredientItemRepository ingredientItemRepository;


	@Test
	void contextLoads() {
		var response = ingredientService.getIngredientCategoryById(1L);
		System.out.println("Success:" + response.isSuccess());
		System.out.println("Success:" + response.getMessage());
		System.out.println("Success:" + response.getData());
		if(response.getData() != null){
			List<IngredientItem>list = (List<IngredientItem>) response.getData();
			System.out.println(list.size());
			list.forEach( ele -> System.out.println("Item:"+ele.getName()+":"+ele.getId()));
		}
	}

	@Test
	void findByRestaurantId(){

//		System.out.println("From derived method");
//		for (IngredientItem ingredientItem : ingredientItemRepository.findByRestaurantId(1L)) {
//			System.out.println(ingredientItem);
//		}

		System.out.println("From QueryMethod method");
		for (IngredientItem ingredientItem : ingredientItemRepository.findIngredientCategoryByRestaurantId(1L)) {
			System.out.println(ingredientItem);
		}


	}



	@Test
	void testWithoutSerialization() {
		long start = System.currentTimeMillis();

		// Direct repository call - no API response
        IngredientItem ingredientItem = ingredientService.updateIngredientItemStockStatus(102L);
        System.out.println(ingredientItem.toString());

		long end = System.currentTimeMillis();
		System.out.println("DB Time Only: " + (end - start) + "ms");
	}

    @Test
    void createIngredientIngre() throws Exception {
            ingredientService.createIngredientItem(1L,"White Sause",1L);

    }


//    @Test
//    void createIngredientIngre() throws Exception {
//        ingredientService.createIngredientItem(1L,"White Sause",1L);
//
//    }












}
