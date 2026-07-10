USE Food;

-- FIX MISSING IMAGES
INSERT IGNORE INTO food_images (food_id, images) VALUES
(5,  'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=600&auto=format&fit=crop'),
(6,  'https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=600&auto=format&fit=crop'),
(11, 'https://images.unsplash.com/photo-1600628421060-909bef21e29a?w=600&auto=format&fit=crop'),
(13, 'https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=600&auto=format&fit=crop'),
(14, 'https://images.unsplash.com/photo-1574653853027-5382a3d23a15?w=600&auto=format&fit=crop'),
(15, 'https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=600&auto=format&fit=crop'),
(16, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop'),
(18, 'https://images.unsplash.com/photo-1598515213692-f8a45609b9bc?w=600&auto=format&fit=crop'),
(19, 'https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=600&auto=format&fit=crop'),
(20, 'https://images.unsplash.com/photo-1594212699903-ec8a3eca368f?w=600&auto=format&fit=crop'),
(21, 'https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb?w=600&auto=format&fit=crop'),
(22, 'https://images.unsplash.com/photo-1543826173-1beeb97525d8?w=600&auto=format&fit=crop'),
(23, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600&auto=format&fit=crop'),
(26, 'https://images.unsplash.com/photo-1559742811-822873691df8?w=600&auto=format&fit=crop'),
(27, 'https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=600&auto=format&fit=crop'),
(28, 'https://images.unsplash.com/photo-1625944525533-473f1a3d54e7?w=600&auto=format&fit=crop'),
(29, 'https://images.unsplash.com/photo-1574653853027-5382a3d23a15?w=600&auto=format&fit=crop'),
(32, 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop'),
(33, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop'),
(34, 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=600&auto=format&fit=crop'),
(36, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&auto=format&fit=crop'),
(37, 'https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=600&auto=format&fit=crop'),
(39, 'https://images.unsplash.com/photo-1557942984-66e4bcdd9568?w=600&auto=format&fit=crop'),
(40, 'https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=600&auto=format&fit=crop'),
(44, 'https://images.unsplash.com/photo-1596797038530-2c107229654b?w=600&auto=format&fit=crop'),
(45, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=600&auto=format&fit=crop'),
(46, 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop'),
(48, 'https://images.unsplash.com/photo-1571997478779-2adcbbe9ab2f?w=600&auto=format&fit=crop'),
(49, 'https://images.unsplash.com/photo-1555126634-323283e090fa?w=600&auto=format&fit=crop'),
(51, 'https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=600&auto=format&fit=crop'),
(54, 'https://images.unsplash.com/photo-1619221882223-12e2c3261213?w=600&auto=format&fit=crop'),
(55, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop'),
(56, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop'),
(59, 'https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=600&auto=format&fit=crop'),
(60, 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop'),
(61, 'https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=600&auto=format&fit=crop'),
(62, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop'),
(63, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop'),
(65, 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600&auto=format&fit=crop'),
(66, 'https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=600&auto=format&fit=crop');

-- NEW RESTAURANTS
INSERT INTO restaurant (id, city, country, description, email, is_open, name, opening_hours, owner_id, registration_number, street, zip_code, cusin_type) VALUES
(9,  'Mumbai',    'India', 'Authentic street-style rolls and wraps',        'rollhub@food.com',    true,  'The Roll Hub',    'Mon-Sun 10AM-11PM', 1, 'REG009', 'Bandra West',     '400050', 'Street Food'),
(10, 'Bengaluru', 'India', 'Premium Korean BBQ and Asian fusion',           'seoulbites@food.com', true,  'Seoul Bites',     'Mon-Sun 12PM-12AM', 1, 'REG010', 'Indiranagar',     '560038', 'Korean'),
(11, 'Hyderabad', 'India', 'New York style thin-crust wood-fired pizzas',   'nypizzaco@food.com',  true,  'NY Pizza Co.',    'Mon-Sun 11AM-11PM', 1, 'REG011', 'Jubilee Hills',   '500033', 'Pizza'),
(12, 'Delhi',     'India', 'Pure vegetarian Rajasthani thali experience',   'rajdhani@food.com',   false, 'Rajdhani Thali', 'Mon-Sun 11AM-10PM', 1, 'REG012', 'Connaught Place', '110001', 'North Indian');

INSERT IGNORE INTO restaurant_images (restaurant_id, images) VALUES
(9,  'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800&auto=format&fit=crop'),
(10, 'https://images.unsplash.com/photo-1590301157890-4810ed352733?w=800&auto=format&fit=crop'),
(11, 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop'),
(12, 'https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=800&auto=format&fit=crop');

-- NEW FOOD ITEMS
INSERT INTO food (id, description, food_category_id, is_available, is_vegetarian, name, price, restaurant_id, seasonal) VALUES
(67, 'Spicy chicken tikka in flaky paratha with mint chutney',      1, true, false, 'Chicken Tikka Roll',     180, 9,  false),
(68, 'Paneer and capsicum in rumali roti with special masala',      1, true, true,  'Paneer Kathi Roll',      150, 9,  false),
(69, 'Mutton seekh kebab in lachha paratha with onion chutney',     1, true, false, 'Seekh Kebab Roll',       220, 9,  false),
(70, 'Egg and spiced potato in golden-fried paratha',               1, true, false, 'Egg Double Roll',        130, 9,  false),
(71, 'Crispy Korean fried chicken with sweet-spicy glaze',          1, true, false, 'Korean Fried Chicken',   380, 10, false),
(72, 'Bibimbap with veggies, gochujang and egg',                    1, true, true,  'Bibimbap Bowl',          320, 10, false),
(73, 'Spicy Korean ramen with pork belly and soft boiled egg',      1, true, false, 'Spicy Ramen',            280, 10, false),
(74, 'Korean BBQ beef bulgogi with rice and kimchi',                1, true, false, 'Beef Bulgogi',           450, 10, false),
(75, 'San Marzano tomatoes, fresh basil, mozzarella on thin crust', 1, true, true,  'Margherita Pizza',       350, 11, false),
(76, 'BBQ chicken with red onion, jalapenos and parmesan',          1, true, false, 'BBQ Chicken Pizza',      480, 11, false),
(77, 'Pepperoni loaded with mozzarella and olives',                 1, true, false, 'Pepperoni Blast',        520, 11, false),
(78, 'Mozzarella, cheddar, parmesan and gouda on thin crust',       1, true, true,  'Four Cheese Pizza',      440, 11, false),
(79, '12-item Rajasthani thali with dal baati churma and dessert',  1, true, true,  'Rajasthani Grand Thali', 420, 12, false),
(80, 'Five lentils slow-cooked with garlic and ghee',               1, true, true,  'Panchmel Dal',           180, 12, false),
(81, 'Baked wheat balls with ghee and sweet churma crumbles',       1, true, true,  'Dal Baati Churma',       320, 12, false),
(82, 'Spiced salted buttermilk with curry leaves and ginger',       1, true, true,  'Chaas',                   80, 12, false);

INSERT IGNORE INTO food_images (food_id, images) VALUES
(67, 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=600&auto=format&fit=crop'),
(68, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop'),
(69, 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop'),
(70, 'https://images.unsplash.com/photo-1600628421060-909bef21e29a?w=600&auto=format&fit=crop'),
(71, 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600&auto=format&fit=crop'),
(72, 'https://images.unsplash.com/photo-1590301157890-4810ed352733?w=600&auto=format&fit=crop'),
(73, 'https://images.unsplash.com/photo-1569050467447-ce54b3bbc37d?w=600&auto=format&fit=crop'),
(74, 'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=600&auto=format&fit=crop'),
(75, 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&auto=format&fit=crop'),
(76, 'https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=600&auto=format&fit=crop'),
(77, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600&auto=format&fit=crop'),
(78, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&auto=format&fit=crop'),
(79, 'https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=600&auto=format&fit=crop'),
(80, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&auto=format&fit=crop'),
(81, 'https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=600&auto=format&fit=crop'),
(82, 'https://images.unsplash.com/photo-1557942984-66e4bcdd9568?w=600&auto=format&fit=crop');

SELECT CONCAT('Food images: ', COUNT(*)) as result FROM food_images;
SELECT CONCAT('Restaurants: ', COUNT(*)) as result FROM restaurant;
SELECT CONCAT('Food items: ',  COUNT(*)) as result FROM food;
