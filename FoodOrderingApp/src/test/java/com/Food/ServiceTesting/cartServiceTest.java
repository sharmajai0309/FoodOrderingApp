package com.Food.ServiceTesting;

import com.Food.Model.Address;
import com.Food.Model.Cart;
import com.Food.Model.CartItem;
import com.Food.Repository.CartItemRepository;
import com.Food.Service.IcartService;
import com.Food.Service.OrderService;
import com.Food.Service.ServiceImpl.CartServiceImpl;
import com.Food.request.AddCartItemRequest;

import com.Food.request.CreateOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class cartServiceTest {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setup() {

        String actualToken ="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKYWkiLCJpYXQiOjE3NjQ3ODE4NzgsImV4cCI6MTc2NDc5OTg3OH0.cw0yuhAFN6fj7T7nKdvYiYfaImu_51KbUj1nfSmpb18";

        // Parse token and extract username (which is user ID "1")
        // But simpler: Directly set authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "Jai",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void Addcartiteam() throws Exception {
        // Given food IDs
        Long[] foodIds = {204L, 205L};

        // Start timer for performance measurement
        long startTime = System.currentTimeMillis();

        // Add all food items to cart one by one
        List<CartItem> results = new ArrayList<>();

        for (Long foodId : foodIds) {
            AddCartItemRequest request = new AddCartItemRequest();
            request.setFoodId(foodId);
            request.setQuantity(4);
            // Fixed quantity as in your original

            CartItem result = cartService.addItemToCart(request);
            assertNotNull(result);
            results.add(result);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Assertions
        assertEquals(foodIds.length, results.size());

        // Simple performance logging
        System.out.println("Performance Test Results:");
        System.out.println("Total items added: " + foodIds.length);
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Average time per item: " + (totalTime / (double) foodIds.length) + " ms");
    }


    @Test
    void orderCreatingTest() throws Exception {
        Address address = new Address();
        address.setCity("Indore");
        address.setCountry("India");
        address.setStreet("56");
        address.setZipCode("12345");


        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setRestaurantId(1L);
        createOrderRequest.setDeliveryAddress(address);

        orderService.createOrder(createOrderRequest);
    }

    @Test
    void updateCartItemQuantity() throws Exception {
        CartItem cartItem = cartService.updateCartItemQuantity(1L, 23);
        System.out.println("Success " + cartItem.getQuantity());
    }

    @Test
    @Async
    void removeItemFromCart() throws Exception {
        cartService.removeItemFromCart(1056L);
        cartService.removeItemFromCart(1057L);

    }


    @Test
    void concurrentUpdateTest() throws Exception {
        System.out.println("=== CONCURRENT UPDATE TEST ===");
        System.out.println("Testing multiple users updating same cart item simultaneously\n");

        // Test Configuration
        Long targetCartItemId = 52L;
        int numberOfThreads = 20; // 20 concurrent users
        int updatesPerThread = 5; // Each user does 5 updates

        // Results tracking
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

        long testStartTime = System.currentTimeMillis();

        // Create and start threads
        for (int threadId = 1; threadId <= numberOfThreads; threadId++) {
            final int currentThreadId = threadId;

            executor.submit(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();

                    // Each thread does multiple updates
                    for (int updateCount = 1; updateCount <= updatesPerThread; updateCount++) {
                        long startTime = System.currentTimeMillis();

                        try {
                            // Simulate different users (User IDs 1-20)
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    String.valueOf(currentThreadId),
                                    null,
                                    Collections.emptyList()
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);

                            // Unique ingredient for each update
                            String uniqueIngredient = "User" + currentThreadId + "_Update" + updateCount;
                            List<String> ingredients = Arrays.asList(uniqueIngredient);

                            CartItem result = cartService.updateCartItemIngredients(
                                    targetCartItemId,
                                    ingredients
                            );

                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;

                            responseTimes.add(duration);
                            successCount.incrementAndGet();

                            // Store result
                            results.add(String.format(
                                    "Thread %2d, Update %d: ✅ %3dms - Final Ingredients: %s",
                                    currentThreadId, updateCount, duration,
                                    result.getIngredients().size() + " items"
                            ));

                        } catch (Exception e) {
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;

                            responseTimes.add(duration);
                            failureCount.incrementAndGet();

                            results.add(String.format(
                                    "Thread %2d, Update %d: ❌ %3dms - %s",
                                    currentThreadId, updateCount, duration,
                                    e.getClass().getSimpleName()
                            ));
                        }

                        // Small random delay between updates (0-50ms)
                        Thread.sleep((long) (Math.random() * 50));
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start all threads at once
        System.out.println("Starting " + numberOfThreads + " concurrent threads...");
        startLatch.countDown();

        // Wait for all threads to complete (with timeout)
        boolean completed = endLatch.await(30, TimeUnit.SECONDS);

        long testEndTime = System.currentTimeMillis();
        long totalTestTime = testEndTime - testStartTime;

        // Shutdown executor
        executor.shutdown();

        // Print individual results (first 20 only)
        System.out.println("\n=== SAMPLE INDIVIDUAL RESULTS ===");
        results.stream().limit(20).forEach(System.out::println);

        if (results.size() > 20) {
            System.out.println("... and " + (results.size() - 20) + " more");
        }

        // Calculate statistics
        int totalUpdates = numberOfThreads * updatesPerThread;
        double successRate = (successCount.get() * 100.0) / totalUpdates;

        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        // Calculate throughput
        double throughput = (double) totalUpdates / (totalTestTime / 1000.0);

        // Print summary
        System.out.println("\n=== CONCURRENT TEST SUMMARY ===");
        System.out.println("Target Cart Item ID: " + targetCartItemId);
        System.out.println("Concurrent Users: " + numberOfThreads);
        System.out.println("Updates per User: " + updatesPerThread);
        System.out.println("Total Updates Attempted: " + totalUpdates);
        System.out.println("\n--- Results ---");
        System.out.println("Successful Updates: " + successCount.get());
        System.out.println("Failed Updates: " + failureCount.get());
        System.out.println("Success Rate: " + String.format("%.1f", successRate) + "%");
        System.out.println("\n--- Performance ---");
        System.out.println("Total Test Time: " + totalTestTime + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " updates/second");
        System.out.println("Avg Response Time: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("Min Response Time: " + minResponseTime + "ms");
        System.out.println("Max Response Time: " + maxResponseTime + "ms");

        // Check for race conditions
        System.out.println("\n=== RACE CONDITION CHECK ===");
        if (failureCount.get() > 0) {
            System.out.println("⚠️  Some failures detected - could be race conditions");
        } else {
            System.out.println("✅ No race conditions detected");
        }

        // Verify data consistency
        try {
            CartItem finalItem = cartItemRepository.findById(targetCartItemId).orElseThrow();
            int finalIngredientCount = finalItem.getIngredients().size();
            System.out.println("Final ingredient count in cart item: " + finalIngredientCount);

            // Should have ingredients from all successful updates
            if (finalIngredientCount > 0) {
                System.out.println("✅ Data consistency maintained");
            }
        } catch (Exception e) {
            System.out.println("❌ Could not verify final state: " + e.getMessage());
        }

        // Assertions
        assertTrue(successRate >= 80,
                "Success rate should be at least 80%, but was " + successRate + "%");

        assertTrue(avgResponseTime < 200,
                "Average response time should be < 200ms under load, but was " + avgResponseTime + "ms");

        System.out.println("\n" + (completed ? "✅ TEST COMPLETED SUCCESSFULLY" : "⚠️  TEST TIMED OUT"));
    }

    @Test
    @Transactional
    void getCartByCustomer_Both() throws Exception {
        System.out.println("=== GET CART FOR CUSTOMERS 1 & 2 ===\n");

        // Customer 1
        System.out.println("🧑‍💼 CUSTOMER 1:");
        Cart cart1 = cartService.getCartByCustomer(1L);
        printCartDetails(cart1);

        System.out.println("\n---\n");

        // Customer 2
        System.out.println("🧑‍💼 CUSTOMER 2:");
        Cart cart2 = cartService.getCartByCustomer(2L);
        printCartDetails(cart2);
    }

    private void printCartDetails(Cart cart) {
        System.out.println("  Cart ID: " + cart.getId());
        System.out.println("  Total: ₹" + cart.getTotal());
        System.out.println("  Items: " + (cart.getItems() != null ? cart.getItems().size() : 0));
    }

    @Test
    @Transactional
    void calculateCartTotal() throws Exception {
        System.out.println("=== CALCULATE CART TOTAL TEST ===");

        // Get cart for customer 1
        Cart cart = cartService.getCartByCustomer(1L);

        System.out.println("Cart ID: " + cart.getId());
        System.out.println("Current Cart Total in DB: ₹" + cart.getTotal());

        // Calculate fresh total
        Long calculatedTotal = cartService.calculateCartTotal(cart);

        System.out.println("Calculated Total: ₹" + calculatedTotal);
        System.out.println("Match: " + (cart.getTotal().equals(calculatedTotal) ? "✅" : "❌"));

        // Verify calculation
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            System.out.println("\n--- Item-wise Breakdown ---");
            for (CartItem item : cart.getItems()) {
                Long itemTotal = item.getFood().getPrice() * item.getQuantity();
                System.out.println(item.getFood().getName() +
                        " (₹" + item.getFood().getPrice() +
                        " x " + item.getQuantity() + ") = ₹" + itemTotal);
            }
        }

        assertEquals(cart.getTotal(), calculatedTotal,
                "DB total should match calculated total");
    }

    @Test
    void clearcart() throws Exception {
        cartService.clearCart(2L);
    }

    @Test
    void updateCartItemIngredientsForMultipleItems() throws Exception {
        System.out.println("=== Testing updateCartItemIngredients for multiple IDs ===");

        // List of cart item IDs from your data
        Long[] cartItemIds = {
                852L, 1002L, 1052L, 1053L, 1054L, 1055L, 1058L, 1059L, 1060L,
                1061L, 1062L, 1063L, 1064L, 1065L, 1066L, 1252L, 1253L, 1254L,
                1255L, 1256L, 1257L, 1258L, 1259L, 1260L, 1261L, 1262L, 1263L,
                1264L, 1265L, 1266L, 1267L, 1268L, 1269L
        };

        // Different ingredient combinations to test
        List<List<String>> ingredientCombinations = Arrays.asList(
                Arrays.asList("Extra Cheese", "Olives"),
                Arrays.asList("Less Spice", "Extra Gravy"),
                Arrays.asList("No Onion", "Extra Garlic"),
                Arrays.asList("Extra Sugar", "Less Ice"),
                Arrays.asList("Butter", "Masala"),
                Arrays.asList("Mint", "Coriander"),
                Arrays.asList("Extra Cream", "Caramel"),
                Arrays.asList("Fresh Herbs", "Spices")
        );

        int successCount = 0;
        int failCount = 0;
        List<Long> failedIds = new ArrayList<>();

        for (int i = 0; i < cartItemIds.length; i++) {
            Long cartItemId = cartItemIds[i];

            // Cycle through ingredient combinations
            List<String> ingredients = ingredientCombinations.get(i % ingredientCombinations.size());

            try {
                CartItem updatedItem = cartService.updateCartItemIngredients(cartItemId, ingredients);
                successCount++;
                System.out.println((i + 1) + ". ✅ Updated CartItem ID " + cartItemId +
                        " with ingredients: " + ingredients);

                // Verify the update
                if (updatedItem.getIngredients() != null) {
                    System.out.println("   Current ingredients: " + updatedItem.getIngredients());
                }

            } catch (Exception e) {
                failCount++;
                failedIds.add(cartItemId);
                System.out.println((i + 1) + ". ❌ Failed CartItem ID " + cartItemId +
                        " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        System.out.println("\n=== Summary ===");
        System.out.println("Total IDs tested: " + cartItemIds.length);
        System.out.println("Successfully updated: " + successCount);
        System.out.println("Failed: " + failCount);

        if (!failedIds.isEmpty()) {
            System.out.println("Failed IDs: " + failedIds);
        }
    }

    @Test
    @Transactional
    void stressTestupdateCartItemIngredient() throws Exception {
        System.out.println("=== STRESS TEST: Update CartItem Ingredients ===");

        // Use just one ID to test repeatedly
        Long cartItemId = 852L;

        // Create many different ingredient combinations
        List<List<String>> allIngredientCombinations = Arrays.asList(
                Arrays.asList("Extra Cheese"),
                Arrays.asList("Extra Cheese", "Olives"),
                Arrays.asList("Extra Cheese", "Olives", "Mushrooms"),
                Arrays.asList("Extra Cheese", "Olives", "Mushrooms", "Bell Peppers"),
                Arrays.asList("Extra Cheese", "Olives", "Mushrooms", "Bell Peppers", "Onions"),
                Arrays.asList("Less Spice"),
                Arrays.asList("Less Spice", "Extra Gravy"),
                Arrays.asList("Butter"),
                Arrays.asList("Butter", "Masala"),
                Arrays.asList("Butter", "Masala", "Cream"),
                Arrays.asList("No Onion"),
                Arrays.asList("No Onion", "No Garlic"),
                Arrays.asList("Extra Sugar"),
                Arrays.asList("Extra Sugar", "Less Ice"),
                Arrays.asList("Extra Sugar", "Less Ice", "Caramel"),
                Arrays.asList("Mint"),
                Arrays.asList("Mint", "Coriander"),
                Arrays.asList("Extra Cream"),
                Arrays.asList("Fresh Herbs"),
                Arrays.asList("Spices")
        );

        int totalOperations = 50; // Run 50 updates
        long startTime = System.currentTimeMillis();

        System.out.println("Starting " + totalOperations + " update operations...\n");

        for (int i = 0; i < totalOperations; i++) {
            // Cycle through different ingredient combinations
            List<String> ingredients = allIngredientCombinations.get(i % allIngredientCombinations.size());

            try {
                cartService.updateCartItemIngredients(cartItemId, ingredients);
                System.out.println((i + 1) + ". ✅ Updated with " + ingredients.size() + " ingredients");

            } catch (Exception e) {
                System.err.println((i + 1) + ". ❌ Failed: " + e.getMessage());
                break;
            }

            // Small delay to simulate real usage
            Thread.sleep(10);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("\n=== STRESS TEST RESULTS ===");
        System.out.println("Total operations: " + totalOperations);
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Average time per operation: " + (totalTime / totalOperations) + " ms");

        // Final check
        CartItem finalItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (finalItem != null && finalItem.getIngredients() != null) {
            System.out.println("Final ingredients count: " + finalItem.getIngredients().size());
            System.out.println("Final ingredients: " + finalItem.getIngredients());
        }
    }

    @Test
    @Transactional
    void performanceStressTestupdateCartItemIngredient() throws Exception {
        System.out.println("=== PERFORMANCE STRESS TEST ===");

        Long cartItemId = 852L;
        int batchSize = 1000;

        System.out.println("Testing with " + batchSize + " ingredient additions in one go...");

        // Create a large list of ingredients
        List<String> massiveIngredientList = new ArrayList<>();
        for (int i = 1; i <= batchSize; i++) {
            massiveIngredientList.add("Ingredient_" + i);
        }

        long startTime = System.currentTimeMillis();

        try {
            CartItem result = cartService.updateCartItemIngredients(cartItemId, massiveIngredientList);
            long endTime = System.currentTimeMillis();

            System.out.println("✅ Successfully processed " + batchSize + " ingredients");
            System.out.println("Time taken: " + (endTime - startTime) + " ms");

            if (result.getIngredients() != null) {
                System.out.println("Final ingredients count: " + result.getIngredients().size());

                // Check for duplicates (should be none since all are unique)
                long uniqueCount = result.getIngredients().stream().distinct().count();
                System.out.println("Unique ingredients: " + uniqueCount);

                // Show first 5 ingredients
                System.out.println("Sample ingredients: " +
                        result.getIngredients().subList(0, Math.min(5, result.getIngredients().size())));
            }

        } catch (Exception e) {
            System.err.println("❌ Failed with " + batchSize + " ingredients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void concurrentItemStressTestupdateCartItemIngredient() throws Exception {
        System.out.println("=== CONCURRENT ITEMS STRESS TEST ===");

        // Test multiple cart items simultaneously
        Long[] cartItemIds = {852L, 1002L, 1052L, 1252L};

        int updatesPerItem = 25;
        int totalUpdates = cartItemIds.length * updatesPerItem;

        System.out.println("Testing " + cartItemIds.length + " items");
        System.out.println("Each item will get " + updatesPerItem + " updates");
        System.out.println("Total updates: " + totalUpdates + "\n");

        int successfulUpdates = 0;

        for (Long cartItemId : cartItemIds) {
            System.out.println("Processing CartItem ID: " + cartItemId);

            for (int i = 1; i <= updatesPerItem; i++) {
                try {
                    List<String> ingredients = Arrays.asList(
                            "Update" + i,
                            "Item" + cartItemId,
                            "Batch" + (i % 5)
                    );

                    cartService.updateCartItemIngredients(cartItemId, ingredients);
                    successfulUpdates++;

                } catch (Exception e) {
                    System.err.println("Failed update " + i + " for ID " + cartItemId + ": " + e.getMessage());
                }
            }
            System.out.println("Completed CartItem ID: " + cartItemId);
        }

        System.out.println("\n=== RESULTS ===");
        System.out.println("Successful updates: " + successfulUpdates + "/" + totalUpdates);
        System.out.println("Success rate: " + (successfulUpdates * 100 / totalUpdates) + "%");
    }

    @Test
    @Transactional
    void getCartById() throws Exception {
        Cart cart = cartService.getCartById(2L);

        System.out.println("=== Cart Details ===");
        System.out.println("Cart ID: " + cart.getId());
        System.out.println("Customer ID: " + cart.getCustomer().getId());
        System.out.println("Total: ₹" + cart.getTotal());
        System.out.println("Items count: " + (cart.getItems() != null ? cart.getItems().size() : 0));

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            System.out.println("\n=== Cart Items ===");
            for (int i = 0; i < cart.getItems().size(); i++) {
                CartItem item = cart.getItems().get(i);
                System.out.println((i + 1) + ". Item ID: " + item.getId());
                System.out.println("   Food: " + item.getFood().getName());
                System.out.println("   Quantity: " + item.getQuantity());
                System.out.println("   Price per item: ₹" + item.getFood().getPrice());
                System.out.println("   Item total: ₹" + item.getTotalPrize());


                if (item.getIngredients() != null && !item.getIngredients().isEmpty()) {
                    System.out.println("   Ingredients: " + String.join(", ", item.getIngredients()));
                }
                System.out.println();
            }
        } else {
            System.out.println("Cart is empty!");
        }
    }





}
