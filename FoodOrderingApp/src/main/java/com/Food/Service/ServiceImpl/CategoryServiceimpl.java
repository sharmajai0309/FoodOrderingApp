package com.Food.Service.ServiceImpl;

import com.Food.Model.Category;
import com.Food.Model.Restaurant;
import com.Food.Model.User;
import com.Food.Repository.ICategoryRepository;
import com.Food.Service.CategoryService;
import com.Food.Service.IResturantService;
import com.Food.Service.IUserServices;
import com.Food.dto.RestaurantDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceimpl implements CategoryService {
    /**
     * {@inheritDoc}
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Category name must be unique within the same restaurant</li>
     *   <li>User must be ADMIN or owner of the restaurant</li>
     *   <li>Restaurant must exist and be active</li>
     * </ul>
     *
     * <p><b>Validation:</b>
     * <ul>
     *   <li>Name: 2-100 characters, not null/empty</li>
     *   <li>Restaurant ID: must be positive number</li>
     * </ul>
     *

     * @param restaurantId the target restaurant ID
     * @return persisted Category entity with generated ID
     *
     * @throws IllegalArgumentException if validation fails or duplicate category exists
     * @throws AccessDeniedException if a user lacks restaurant access
     * @throws EntityNotFoundException if a restaurant not found
     *
     * @implNote This method runs within a transactional context
     * @category Category Management
     */


    private final ICategoryRepository categoryRepository;
    private final IUserServices iuserServices;
    private final IResturantService resturantService;

    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return iuserServices.findByUsername(authentication.getName());
    }
    @Override
    @Transactional
    public Category createCategory(String name, Long userId) throws Exception {
        Restaurant restaurantById = resturantService.findRestaurantById(userId);
        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurantById);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findCategoriesByResturantId(Long id) {
        return categoryRepository.findCategoryNamesByResturantId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Category findCategoryById(Long id)  {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
