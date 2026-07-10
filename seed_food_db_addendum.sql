-- ============================================================
-- Addendum script to fill the remaining empty tables:
-- user_favorite, food_ingredients, order_item_ingredients, 
-- cart_item_ingredients
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE user_favorite;
TRUNCATE TABLE food_ingredients;
TRUNCATE TABLE order_item_ingredients;
TRUNCATE TABLE cart_item_ingredients;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. USER FAVORITES (user_id, favorite_id)
-- user_id references users.id, favorite_id references restaurant.id
INSERT INTO user_favorite (user_id, favorite_id) VALUES
(52, 8),
(155, 2),
(155, 1),
(156, 5),
(157, 6);

-- 2. FOOD INGREDIENTS (food_id, ingredients_id)
-- food_id references food.id, ingredients_id references ingredient_item.id
INSERT INTO food_ingredients (food_id, ingredients_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 1), (2, 2), (2, 3), (2, 5),
(3, 1), (3, 2), (3, 3), (3, 6),
(9, 7), (9, 8), (9, 9), (9, 10), (9, 11),
(10, 7), (10, 8), (10, 9), (10, 10), (10, 11),
(17, 12), (17, 13), (17, 14),
(18, 12), (18, 13), (18, 14),
(20, 15), (20, 16),
(21, 15), (21, 16),
(24, 17), (24, 18),
(25, 17), (25, 18),
(30, 19), (31, 19),
(38, 20),
(41, 21), (41, 22), (41, 23),
(42, 21), (42, 22), (42, 23),
(57, 24), (57, 25), (57, 26),
(58, 24), (58, 25), (58, 26);

-- 3. ORDER ITEM INGREDIENTS (order_item_id, ingredients)
-- order_item_id references order_item.id
INSERT INTO order_item_ingredients (order_item_id, ingredients) VALUES
(1, 'Spicy'),
(1, 'Extra Raita'),
(2, 'Butter'),
(3, 'Hot'),
(7, 'Warm'),
(8, 'Medium'),
(11, 'Rumali Roti'),
(13, 'Hot'),
(14, 'Medium');

-- 4. CART ITEM INGREDIENTS (cart_item_id, ingredients)
-- cart_item_id references cart_item.id
INSERT INTO cart_item_ingredients (cart_item_id, ingredients) VALUES
(1, 'Spicy'),
(1, 'Extra Raita'),
(3, 'Pan Fried');
