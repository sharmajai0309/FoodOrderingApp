-- Script to delete all users except Jai (id = 1)
-- Handle foreign key dependencies first

-- 1. Reassign all restaurants to Jai (so they aren't deleted)
UPDATE restaurant SET owner_id = 1 WHERE owner_id != 1 OR owner_id IS NULL;

-- 2. Delete user favorites for all other users
DELETE FROM user_favorite WHERE user_id != 1;

-- 3. Delete carts (and their items) for other users
DELETE FROM cart_item_ingredients WHERE cart_item_id IN (SELECT id FROM cart_item WHERE cart_id IN (SELECT id FROM cart WHERE customer_id != 1));
DELETE FROM cart_item WHERE cart_id IN (SELECT id FROM cart WHERE customer_id != 1);
DELETE FROM cart WHERE customer_id != 1;

-- 4. Reassign all existing orders to Jai so we keep the rich order history
UPDATE orders SET customer_id = 1 WHERE customer_id != 1;

-- Wait, delivery addresses of those orders were tied to the other users.
-- Let's unbind the addresses from the users so they aren't deleted when the user is deleted, OR just reassign them to Jai.
-- However, an address might not have a unique constraint on user_id, but logically it's better to reassign or set null.
UPDATE address SET user_id = 1 WHERE user_id != 1 AND user_id IS NOT NULL;

-- 5. Finally, delete the users
DELETE FROM users WHERE id != 1;
