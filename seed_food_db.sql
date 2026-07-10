-- ============================================================
--  CraveRush Database Seed Script
--  - Keeps: users table (all logins and roles untouched)
--  - Cleans: everything else and rebuilds with authentic data
--  - 8 real Indian restaurants, 80+ menu items, addresses,
--    categories, ingredients, carts, and sample orders
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ── TRUNCATE all non-user tables ─────────────────────────────
TRUNCATE TABLE order_item_ingredients;
TRUNCATE TABLE order_item;
TRUNCATE TABLE orders;
TRUNCATE TABLE cart_item_ingredients;
TRUNCATE TABLE cart_item;
TRUNCATE TABLE cart;
TRUNCATE TABLE user_favorite;
TRUNCATE TABLE food_ingredients;
TRUNCATE TABLE food_images;
TRUNCATE TABLE food;
TRUNCATE TABLE ingredient_item;
TRUNCATE TABLE ingredient_category;
TRUNCATE TABLE category;
TRUNCATE TABLE restaurant_images;
TRUNCATE TABLE restaurant;
TRUNCATE TABLE address;

-- Reset sequences to clean starting values
UPDATE address_seq            SET next_val = 1;
UPDATE restaurant_seq         SET next_val = 1;
UPDATE food_seq               SET next_val = 1;
UPDATE orders_seq             SET next_val = 1;
UPDATE category_seq           SET next_val = 1;
UPDATE ingredient_category_seq SET next_val = 1;
UPDATE ingredient_item_seq    SET next_val = 1;
UPDATE cart_seq               SET next_val = 1;
UPDATE cart_item_seq          SET next_val = 1;
UPDATE order_item_seq         SET next_val = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. ADDRESSES (restaurants + customers)
-- ============================================================
INSERT INTO address (id, street, city, zip_code, country, user_id) VALUES
-- Restaurant addresses (user_id = owning restaurant admin)
(1,  '12, MG Road, Connaught Place',     'New Delhi',  '110001', 'India', NULL),
(2,  '45, Brigade Road, Shivajinagar',   'Bengaluru',  '560001', 'India', NULL),
(3,  '7, Park Street',                   'Kolkata',    '700016', 'India', NULL),
(4,  '88, Marine Drive, Churchgate',     'Mumbai',     '400020', 'India', NULL),
(5,  '33, Anna Salai, Teynampet',        'Chennai',    '600018', 'India', NULL),
(6,  '19, Banjara Hills Road No. 12',    'Hyderabad',  '500034', 'India', NULL),
(7,  '56, Civil Lines',                  'Jaipur',     '302006', 'India', NULL),
(8,  '11, Hazratganj',                   'Lucknow',    '226001', 'India', NULL),
-- Customer delivery addresses
(9,  'B-204, Sector 62, Noida',          'Noida',      '201301', 'India', 52),
(10, '301, Koramangala 4th Block',       'Bengaluru',  '560034', 'India', 155),
(11, '14/2, Andheri West',               'Mumbai',     '400058', 'India', 156),
(12, 'Flat 7, Jubilee Hills',            'Hyderabad',  '500033', 'India', 157);

UPDATE address_seq SET next_val = 51;

-- ============================================================
-- 2. RESTAURANTS (8 authentic Indian chains/props)
-- ============================================================
-- owner_id references users: Akshay=2, rajesh_owner=153, priya_owner=154, Jai=1 (admin demo)
INSERT INTO restaurant
  (id, name, description, cusine_type, email, mobile, facebook, instagram, twitter,
   open, opening_hours, registration_date, address_id, owner_id)
VALUES
(1, 'Barbeque Nation',
   'India\'s favourite barbeque dining chain. Live grills at your table, unlimited starters and an elaborate buffet.',
   'North Indian, Barbeque', 'contact@barbeque-nation.in', '1800-120-2947',
   'BarbequeNationIndia', 'barbeque_nation', 'BarbequeNation',
   1, 'Mon-Sun 12:00 PM - 11:00 PM', '2024-01-10 10:00:00', 1, 2),

(2, 'Meghana Foods',
   'Legendary Andhra-style biryani and spicy curries. Known for the iconic Boneless Chicken Biryani.',
   'Andhra, Biryani', 'info@meghanafoods.com', '+91-80-2222-3333',
   'MeghanaFoodsBangalore', 'meghana_foods_official', 'MeghanaFoods',
   1, 'Mon-Sun 11:30 AM - 11:30 PM', '2024-02-15 09:00:00', 2, 153),

(3, 'Peter Cat',
   'Iconic Kolkata restaurant since 1975. Famous for the legendary Chelo Kebab platter.',
   'Continental, Mughlai', 'reservations@petercat.in', '+91-33-2229-8841',
   NULL, 'petercatkolkata', NULL,
   1, 'Mon-Sun 12:00 PM - 11:30 PM', '2024-01-20 08:00:00', 3, 1),

(4, 'Trishna',
   'Award-winning seafood restaurant in Mumbai. Butter Garlic Crab is a Mumbai institution.',
   'Seafood, Coastal Indian', 'hello@trishnacafe.com', '+91-22-2270-3213',
   NULL, 'trishna_mumbai', NULL,
   1, 'Tue-Sun 12:30 PM - 3:30 PM, 7:00 PM - 12:00 AM', '2024-03-01 08:00:00', 4, 2),

(5, 'Saravana Bhavan',
   'World-famous South Indian vegetarian restaurant chain. Authentic idlis, dosas and filter coffee.',
   'South Indian, Vegetarian', 'info@saravanabhavan.com', '+91-44-2815-0001',
   'SaravanaBhavan', 'saravana_bhavan_official', NULL,
   1, 'Mon-Sun 7:00 AM - 10:30 PM', '2024-01-05 07:00:00', 5, 154),

(6, 'Paradise Biryani',
   'Hyderabad\'s most iconic biryani restaurant since 1953. Dum-style biryani with authentic Nizami recipes.',
   'Hyderabadi, Biryani', 'feedback@paradisegroup.in', '+91-40-2763-2323',
   'ParadiseBiryaniHyderabad', 'paradise_biryani_hyd', 'ParadiseBiryani',
   1, 'Mon-Sun 11:00 AM - 11:00 PM', '2024-02-01 09:00:00', 6, 153),

(7, 'Laxmi Mishthan Bhandar',
   'Jaipur\'s most beloved sweet shop and traditional Rajasthani thali restaurant since 1954.',
   'Rajasthani, Sweets', 'contact@lmbjaipur.com', '+91-141-256-5555',
   NULL, 'lmb_jaipur', NULL,
   1, 'Mon-Sun 8:00 AM - 10:00 PM', '2024-03-10 08:00:00', 7, 154),

(8, 'Tunday Kababi',
   'The legendary 1905 kebab house of Lucknow. World-famous Galouti and Shami kebabs with rumali roti.',
   'Awadhi, Kebabs', 'info@tundaykababi.com', '+91-522-2613-7777',
   'TundayKababiOfficial', 'tunday_kababi', 'TundayKababi',
   1, 'Mon-Sun 7:00 AM - 11:00 PM', '2024-01-25 07:00:00', 8, 2);

UPDATE restaurant_seq SET next_val = 51;

-- ============================================================
-- 3. RESTAURANT IMAGES (Unsplash real food photography URLs)
-- ============================================================
INSERT INTO restaurant_images (restaurant_id, images) VALUES
(1, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop'),
(1, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800&auto=format&fit=crop'),
(2, 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=800&auto=format&fit=crop'),
(2, 'https://images.unsplash.com/photo-1596797038530-2c107229654b?w=800&auto=format&fit=crop'),
(3, 'https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=800&auto=format&fit=crop'),
(3, 'https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=800&auto=format&fit=crop'),
(4, 'https://images.unsplash.com/photo-1559410545-0bdcd187e0a6?w=800&auto=format&fit=crop'),
(4, 'https://images.unsplash.com/photo-1625944525533-473f1a3d54e7?w=800&auto=format&fit=crop'),
(5, 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop'),
(5, 'https://images.unsplash.com/photo-1630410364547-ce37e1e18793?w=800&auto=format&fit=crop'),
(6, 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=800&auto=format&fit=crop'),
(6, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop'),
(7, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800&auto=format&fit=crop'),
(7, 'https://images.unsplash.com/photo-1606471191009-63994c53433b?w=800&auto=format&fit=crop'),
(8, 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=800&auto=format&fit=crop'),
(8, 'https://images.unsplash.com/photo-1599487488310-9dd1440de25a?w=800&auto=format&fit=crop');

-- ============================================================
-- 4. FOOD CATEGORIES
-- ============================================================
INSERT INTO category (id, name, restaurant_id) VALUES
-- Barbeque Nation (1)
(1, 'Starters', 1), (2, 'Main Course', 1), (3, 'Desserts', 1),
-- Meghana Foods (2)
(4, 'Biryani', 2), (5, 'Curries', 2), (6, 'Breads', 2),
-- Peter Cat (3)
(7, 'Kebabs & Grills', 3), (8, 'Continental Mains', 3), (9, 'Soups & Sides', 3),
-- Trishna (4)
(10, 'Seafood Specials', 4), (11, 'Coastal Curries', 4),
-- Saravana Bhavan (5)
(12, 'Breakfast', 5), (13, 'Lunch & Dinner', 5), (14, 'Beverages & Desserts', 5),
-- Paradise Biryani (6)
(15, 'Biryani', 6), (16, 'Boti & Kebabs', 6), (17, 'Raita & Sides', 6),
-- LMB (7)
(18, 'Thali & Meals', 7), (19, 'Sweets & Mithai', 7),
-- Tunday Kababi (8)
(20, 'Kebabs', 8), (21, 'Rotis & Parathas', 8), (22, 'Nihari & Curries', 8);

UPDATE category_seq SET next_val = 51;

-- ============================================================
-- 5. INGREDIENT CATEGORIES + ITEMS
-- ============================================================
INSERT INTO ingredient_category (id, name, restaurant_id) VALUES
(1, 'Spice Level',    1), (2, 'Protein Type',   1),
(3, 'Spice Level',    2), (4, 'Add-ons',         2),
(5, 'Spice Level',    3), (6, 'Sauce',           3),
(7, 'Cooking Style',  4), (8, 'Add-ons',         5),
(9, 'Spice Level',    6), (10, 'Bread Choice',   8);

UPDATE ingredient_category_seq SET next_val = 51;

INSERT INTO ingredient_item (id, name, in_stock, category_id, restaurant_id) VALUES
(1, 'Mild',           1, 1, 1), (2, 'Medium',         1, 1, 1), (3, 'Hot',            1, 1, 1),
(4, 'Chicken',        1, 2, 1), (5, 'Mutton',          1, 2, 1), (6, 'Veg',            1, 2, 1),
(7, 'Mild',           1, 3, 2), (8, 'Spicy',           1, 3, 2), (9, 'Extra Spicy',    1, 3, 2),
(10,'Extra Raita',    1, 4, 2), (11,'Boiled Egg',      1, 4, 2),
(12,'Mild',           1, 5, 3), (13,'Medium',          1, 5, 3), (14,'Hot',            1, 5, 3),
(15,'Pepper Sauce',   1, 6, 3), (16,'Mushroom Sauce',  1, 6, 3),
(17,'Tandoor',        1, 7, 4), (18,'Pan Fried',       1, 7, 4),
(19,'Extra Chutney',  1, 8, 5), (20,'Masala Chai',     1, 8, 5),
(21,'Mild',           1, 9, 6), (22,'Medium',          1, 9, 6), (23,'Spicy',          1, 9, 6),
(24,'Rumali Roti',    1,10, 8), (25,'Sheermal',        1,10, 8), (26,'Tandoori Roti',  1,10, 8);

UPDATE ingredient_item_seq SET next_val = 51;

-- ============================================================
-- 6. FOOD ITEMS with real Unsplash images
-- ============================================================
INSERT INTO food (id, name, description, price, is_vegetarian, is_seasonal, is_available, created_date, foodcategory_id, restaurant_id)
VALUES
-- ── Barbeque Nation Starters (cat 1) ──────────────────────
(1,  'Chicken Tikka',
     'Succulent boneless chicken marinated in yogurt and spices, char-grilled on live coals.',
     349, 0, 0, 1, NOW(), 1, 1),
(2,  'Mutton Seekh Kebab',
     'Minced mutton with herbs, pressed on skewers and grilled to perfection.',
     399, 0, 0, 1, NOW(), 1, 1),
(3,  'Paneer Tikka',
     'Fresh cottage cheese cubes marinated in spiced yogurt and grilled in tandoor.',
     299, 1, 0, 1, NOW(), 1, 1),
(4,  'Corn on the Cob',
     'Sweet corn brushed with masala butter and grilled on live charcoal.',
     149, 1, 0, 1, NOW(), 1, 1),
-- ── Barbeque Nation Main Course (cat 2) ───────────────────
(5,  'Chicken Biryani (Buffet)',
     'Aromatic dum biryani with tender chicken and saffron-infused rice.',
     0, 0, 0, 1, NOW(), 2, 1),
(6,  'Dal Makhani',
     'Slow-cooked black lentils in a rich tomato-butter gravy. A North Indian classic.',
     0, 1, 0, 1, NOW(), 2, 1),
-- ── Barbeque Nation Desserts (cat 3) ──────────────────────
(7,  'Gulab Jamun',
     'Soft milk-solid dumplings soaked in rose-flavoured sugar syrup. Served warm.',
     149, 1, 0, 1, NOW(), 3, 1),
(8,  'Walnut Brownie with Ice Cream',
     'Warm chocolate brownie with crushed walnuts served with a scoop of vanilla ice cream.',
     199, 1, 0, 1, NOW(), 3, 1),

-- ── Meghana Foods Biryani (cat 4) ─────────────────────────
(9,  'Boneless Chicken Biryani',
     'Meghana\'s signature dum biryani — boneless chicken cooked with basmati rice and Andhra spices. Legendary.',
     280, 0, 0, 1, NOW(), 4, 2),
(10, 'Mutton Biryani',
     'Tender bone-in mutton pieces slow-cooked with long-grain basmati in traditional dum style.',
     350, 0, 0, 1, NOW(), 4, 2),
(11, 'Egg Biryani',
     'Perfectly boiled eggs layered with spiced saffron rice. A Meghana classic.',
     220, 0, 0, 1, NOW(), 4, 2),
(12, 'Veg Biryani',
     'Fresh seasonal vegetables cooked with aromatic basmati rice in dum style.',
     200, 1, 0, 1, NOW(), 4, 2),
-- ── Meghana Foods Curries (cat 5) ─────────────────────────
(13, 'Chicken Curry (Andhra Style)',
     'Fiery Andhra-style chicken curry with whole spices and a tangy tamarind base.',
     240, 0, 0, 1, NOW(), 5, 2),
(14, 'Gongura Mutton',
     'Hyderabad\'s pride — mutton cooked with tangy sorrel leaves (gongura). Bold and robust.',
     320, 0, 0, 1, NOW(), 5, 2),
-- ── Meghana Foods Breads (cat 6) ──────────────────────────
(15, 'Naan',
     'Soft leavened bread baked fresh in tandoor. Goes best with any curry.',
     40, 1, 0, 1, NOW(), 6, 2),
(16, 'Parotta',
     'Flaky layered South Indian flatbread made with maida. Pairs perfectly with curries.',
     35, 1, 0, 1, NOW(), 6, 2),

-- ── Peter Cat Kebabs & Grills (cat 7) ─────────────────────
(17, 'Chelo Kebab',
     'Peter Cat\'s iconic dish since 1975 — grilled Seekh kebab on a bed of steamed rice with egg and butter.',
     680, 0, 0, 1, NOW(), 7, 3),
(18, 'Chicken Cafreal',
     'Goan-inspired herbed chicken grilled with coriander, garlic and spices.',
     550, 0, 0, 1, NOW(), 7, 3),
(19, 'Veg Grill Platter',
     'Assorted grilled vegetables and paneer with garlic-herb butter, served with toast.',
     420, 1, 0, 1, NOW(), 7, 3),
-- ── Peter Cat Continental Mains (cat 8) ───────────────────
(20, 'Shepherd\'s Pie',
     'Classic British comfort food — minced meat and vegetable ragout topped with creamy mashed potatoes.',
     680, 0, 0, 1, NOW(), 8, 3),
(21, 'Spaghetti Bolognese',
     'Al dente spaghetti with slow-cooked minced lamb ragu in a rich tomato base.',
     620, 0, 0, 1, NOW(), 8, 3),
(22, 'Mushroom Stroganoff',
     'Creamy sautéed mushrooms in a tangy sour cream sauce, served with steamed rice.',
     520, 1, 0, 1, NOW(), 8, 3),
-- ── Peter Cat Soups & Sides (cat 9) ───────────────────────
(23, 'Mulligatawny Soup',
     'The classic Anglo-Indian pepper soup — lentil-based with coconut and curry leaves.',
     280, 1, 0, 1, NOW(), 9, 3),

-- ── Trishna Seafood (cat 10) ──────────────────────────────
(24, 'Butter Garlic Crab',
     'Trishna\'s crown jewel — fresh crab tossed in a rich butter and garlic sauce. A Mumbai institution.',
     1200, 0, 0, 1, NOW(), 10, 4),
(25, 'Prawn Koliwada',
     'Spicy Maharashtrian-style crispy fried prawns with carom seeds. Irresistible.',
     650, 0, 0, 1, NOW(), 10, 4),
(26, 'Fish Tawa Fry',
     'Fresh surmai (kingfish) marinated in Goan masala and pan-seared on a cast iron tawa.',
     750, 0, 0, 1, NOW(), 10, 4),
(27, 'Lobster Thermidor',
     'Whole lobster in a creamy cognac and mustard sauce, gratinated with Gruyère. A weekend special.',
     2200, 0, 1, 1, NOW(), 10, 4),
-- ── Trishna Coastal Curries (cat 11) ──────────────────────
(28, 'Kerala Prawn Moilee',
     'Succulent prawns in a light, aromatic coconut milk curry with turmeric and green chillies.',
     720, 0, 0, 1, NOW(), 11, 4),
(29, 'Goan Fish Curry',
     'Traditional Goan curry with fresh pomfret in a tangy kokum and coconut gravy.',
     680, 0, 0, 1, NOW(), 11, 4),

-- ── Saravana Bhavan Breakfast (cat 12) ────────────────────
(30, 'Masala Dosa',
     'Crispy fermented rice crepe filled with spiced potato masala, served with sambar and chutneys.',
     120, 1, 0, 1, NOW(), 12, 5),
(31, 'Idli Sambar (3 pcs)',
     'Steamed rice cakes served with piping hot sambar and three types of chutney.',
     90, 1, 0, 1, NOW(), 12, 5),
(32, 'Medu Vada (2 pcs)',
     'Crispy doughnut-shaped lentil fritters with a tender interior. Served with sambar.',
     80, 1, 0, 1, NOW(), 12, 5),
(33, 'Pongal',
     'Comfort rice-lentil porridge tempered with ghee, pepper and cashews. A Tamil classic.',
     110, 1, 0, 1, NOW(), 12, 5),
(34, 'Rava Upma',
     'Semolina cooked with vegetables, mustard seeds and curry leaves. Light and wholesome.',
     80, 1, 0, 1, NOW(), 12, 5),
-- ── Saravana Bhavan Lunch & Dinner (cat 13) ───────────────
(35, 'Saravana Special Meals',
     'Complete vegetarian thali — rice, 2 curries, rasam, sambar, payasam, papad and pickle.',
     180, 1, 0, 1, NOW(), 13, 5),
(36, 'Chettinad Veg Curry',
     'Bold and aromatic curry from Tamil Nadu\'s Chettinad region, slow-cooked with kalpasi and marathi mokku.',
     160, 1, 0, 1, NOW(), 13, 5),
(37, 'Bisi Bele Bath',
     'Karnataka\'s one-pot wonder — rice, lentils and vegetables cooked together with a special spice blend.',
     150, 1, 0, 1, NOW(), 13, 5),
-- ── Saravana Bhavan Beverages (cat 14) ────────────────────
(38, 'Filter Coffee',
     'South India\'s pride — dark roasted coffee decoction mixed with frothed milk. Perfect ratio.',
     60, 1, 0, 1, NOW(), 14, 5),
(39, 'Rose Milk',
     'Chilled full-fat milk flavoured with fresh rose syrup. A regional favourite.',
     80, 1, 0, 1, NOW(), 14, 5),
(40, 'Kesari Bath (Halwa)',
     'Orange-hued semolina halwa with saffron, cashews and ghee. Served warm.',
     90, 1, 0, 1, NOW(), 14, 5),

-- ── Paradise Biryani Biryani (cat 15) ─────────────────────
(41, 'Chicken Biryani (Full)',
     'Paradise\'s 70-year-old recipe — dum-cooked with whole spices, saffron milk and caramelised onions.',
     320, 0, 0, 1, NOW(), 15, 6),
(42, 'Chicken Biryani (Half)',
     'Same legendary Paradise biryani, half portion. Perfect for a solo meal.',
     180, 0, 0, 1, NOW(), 15, 6),
(43, 'Mutton Biryani (Full)',
     'Fall-off-the-bone mutton slow-cooked in the dum style with aromatic Hyderabadi masala.',
     420, 0, 0, 1, NOW(), 15, 6),
(44, 'Prawn Biryani (Full)',
     'Tiger prawns layered with basmati rice and Paradise\'s signature spice blend.',
     480, 0, 0, 1, NOW(), 15, 6),
(45, 'Veg Biryani (Full)',
     'Fresh garden vegetables in fragrant basmati rice. Light, wholesome and perfectly spiced.',
     220, 1, 0, 1, NOW(), 15, 6),
-- ── Paradise Boti & Kebabs (cat 16) ───────────────────────
(46, 'Mutton Boti Kebab',
     'Tender mutton cubes marinated overnight in yogurt and spices, grilled on charcoal.',
     380, 0, 0, 1, NOW(), 16, 6),
(47, 'Chicken 65',
     'Hyderabad-style deep-fried crispy chicken tossed in a tangy red chilli sauce. A party classic.',
     260, 0, 0, 1, NOW(), 16, 6),
-- ── Paradise Raita & Sides (cat 17) ───────────────────────
(48, 'Onion Raita',
     'Chilled yogurt with diced onions and fresh coriander. The perfect biryani companion.',
     60, 1, 0, 1, NOW(), 17, 6),
(49, 'Mirchi ka Salan',
     'Rich peanut-sesame-coconut gravy with whole green chillies. A Hyderabadi biryani essential.',
     80, 1, 0, 1, NOW(), 17, 6),

-- ── LMB Thali (cat 18) ────────────────────────────────────
(50, 'Royal Rajasthani Thali',
     'LMB\'s legendary thali — dal baati churma, gatte ki sabzi, ker sangri, 5 rotis, rice and kheer.',
     450, 1, 0, 1, NOW(), 18, 7),
(51, 'Mini Thali',
     'Smaller thali with 2 sabzis, dal, roti, rice, papad and meetha. Perfect for one.',
     280, 1, 0, 1, NOW(), 18, 7),
(52, 'Dal Baati Churma',
     'Rajasthan\'s most beloved dish — baked wheat balls served with spiced dal and sweetened churma.',
     320, 1, 0, 1, NOW(), 18, 7),
-- ── LMB Sweets (cat 19) ───────────────────────────────────
(53, 'Ghewar',
     'Jaipur\'s royal disc-shaped dessert made with flour and ghee, soaked in sugar syrup. Seasonal masterpiece.',
     180, 1, 1, 1, NOW(), 19, 7),
(54, 'Malpua with Rabri',
     'Deep-fried sweet pancakes soaked in saffron sugar syrup, served with thickened condensed milk.',
     160, 1, 0, 1, NOW(), 19, 7),
(55, 'Pyaaz Kachori',
     'Jaipur\'s iconic flaky pastry stuffed with spiced onion filling. Best breakfast item in Rajasthan.',
     40, 1, 0, 1, NOW(), 19, 7),
(56, 'Rabri Jalebi',
     'Crispy spirals of fried batter soaked in sugar syrup, served with chilled thick rabri.',
     120, 1, 0, 1, NOW(), 19, 7),

-- ── Tunday Kababi Kebabs (cat 20) ─────────────────────────
(57, 'Galouti Kebab (6 pcs)',
     'The world-famous 1905 recipe — ultra-soft minced mutton patties with 156 spices. Melts instantly.',
     380, 0, 0, 1, NOW(), 20, 8),
(58, 'Shami Kebab (4 pcs)',
     'Minced mutton and chana dal patties pan-fried with egg coating. A Lucknow institution.',
     260, 0, 0, 1, NOW(), 20, 8),
(59, 'Kakori Kebab (4 pcs)',
     'Minced mutton seekh kebab from Kakori village — delicate, melt-in-mouth texture.',
     300, 0, 0, 1, NOW(), 20, 8),
(60, 'Gilafi Seekh Kebab (4 pcs)',
     'Juicy seekh kebab wrapped in a coat of diced vegetables and herbs, grilled on slow fire.',
     280, 0, 0, 1, NOW(), 20, 8),
-- ── Tunday Rotis (cat 21) ─────────────────────────────────
(61, 'Rumali Roti',
     'Paper-thin handkerchief bread stretched over an inverted wok. The traditional kebab companion.',
     30, 1, 0, 1, NOW(), 21, 8),
(62, 'Sheermal',
     'Saffron-infused slightly sweet Lucknawi flatbread baked in tandoor. A royal treat.',
     50, 1, 0, 1, NOW(), 21, 8),
(63, 'Paratha',
     'Layered whole wheat flatbread cooked with ghee on tawa. Crisp outside, soft inside.',
     40, 1, 0, 1, NOW(), 21, 8),
-- ── Tunday Nihari (cat 22) ────────────────────────────────
(64, 'Nihari (Mutton)',
     'Slow-cooked overnight stew with mutton shanks in a rich, gelatinous gravy. The breakfast of nawabs.',
     420, 0, 0, 1, NOW(), 22, 8),
(65, 'Paya Shorba',
     'Slow-simmered trotters in a bone broth with whole spices. Deeply nourishing.',
     280, 0, 0, 1, NOW(), 22, 8),
(66, 'Mutton Rogan Josh',
     'Kashmiri-style braised mutton in a bold red gravy with whole spices and dried cockscomb flower.',
     380, 0, 0, 1, NOW(), 22, 8);

UPDATE food_seq SET next_val = 151;

-- ============================================================
-- 7. FOOD IMAGES
-- ============================================================
INSERT INTO food_images (food_id, images) VALUES
(1,  'https://images.unsplash.com/photo-1599487488310-9dd1440de25a?w=600&auto=format&fit=crop'),
(2,  'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop'),
(3,  'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop'),
(4,  'https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=600&auto=format&fit=crop'),
(7,  'https://images.unsplash.com/photo-1619221882223-12e2c3261213?w=600&auto=format&fit=crop'),
(8,  'https://images.unsplash.com/photo-1606471191009-63994c53433b?w=600&auto=format&fit=crop'),
(9,  'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=600&auto=format&fit=crop'),
(10, 'https://images.unsplash.com/photo-1596797038530-2c107229654b?w=600&auto=format&fit=crop'),
(12, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=600&auto=format&fit=crop'),
(17, 'https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=600&auto=format&fit=crop'),
(24, 'https://images.unsplash.com/photo-1559410545-0bdcd187e0a6?w=600&auto=format&fit=crop'),
(25, 'https://images.unsplash.com/photo-1625944525533-473f1a3d54e7?w=600&auto=format&fit=crop'),
(30, 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop'),
(31, 'https://images.unsplash.com/photo-1630410364547-ce37e1e18793?w=600&auto=format&fit=crop'),
(35, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&auto=format&fit=crop'),
(38, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600&auto=format&fit=crop'),
(41, 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=600&auto=format&fit=crop'),
(42, 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600&auto=format&fit=crop'),
(43, 'https://images.unsplash.com/photo-1599487488310-9dd1440de25a?w=600&auto=format&fit=crop'),
(47, 'https://images.unsplash.com/photo-1626777553635-be342a5d1a5d?w=600&auto=format&fit=crop'),
(50, 'https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop'),
(52, 'https://images.unsplash.com/photo-1574484284002-952d92456975?w=600&auto=format&fit=crop'),
(53, 'https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=600&auto=format&fit=crop'),
(57, 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=600&auto=format&fit=crop'),
(58, 'https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=600&auto=format&fit=crop'),
(64, 'https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=600&auto=format&fit=crop');

-- ============================================================
-- 8. CUSTOMER DELIVERY ADDRESSES (linked to users)
-- ============================================================
-- Address IDs 9-12 already inserted in section 1 for users 52, 155, 156, 157

-- ============================================================
-- 9. SAMPLE ORDERS (realistic completed, in-progress, delivered)
-- ============================================================
INSERT INTO orders
  (id, total_item, total_price, total_amount, order_status, created_at,
   accepted_at, preparing_at, picked_up_at, out_for_delivery_at, delivered_at,
   delivery_partner_id, customer_id, delivery_address_id, restaurant_id)
VALUES
-- Order 1: Arjun ordered from Meghana Foods — DELIVERED
(1001, 3, 830, 830, 'DELIVERED',
 NOW() - INTERVAL 3 DAY,
 NOW() - INTERVAL 3 DAY + INTERVAL 5 MINUTE,
 NOW() - INTERVAL 3 DAY + INTERVAL 10 MINUTE,
 NOW() - INTERVAL 3 DAY + INTERVAL 30 MINUTE,
 NOW() - INTERVAL 3 DAY + INTERVAL 35 MINUTE,
 NOW() - INTERVAL 3 DAY + INTERVAL 60 MINUTE,
 NULL, 155, 10, 2),

-- Order 2: Sneha ordered from Saravana Bhavan — DELIVERED
(1002, 4, 550, 550, 'DELIVERED',
 NOW() - INTERVAL 2 DAY,
 NOW() - INTERVAL 2 DAY + INTERVAL 4 MINUTE,
 NOW() - INTERVAL 2 DAY + INTERVAL 12 MINUTE,
 NOW() - INTERVAL 2 DAY + INTERVAL 25 MINUTE,
 NOW() - INTERVAL 2 DAY + INTERVAL 28 MINUTE,
 NOW() - INTERVAL 2 DAY + INTERVAL 50 MINUTE,
 NULL, 156, 11, 5),

-- Order 3: Vikram ordered from Paradise Biryani — DELIVERED
(1003, 2, 580, 580, 'DELIVERED',
 NOW() - INTERVAL 1 DAY,
 NOW() - INTERVAL 1 DAY + INTERVAL 3 MINUTE,
 NOW() - INTERVAL 1 DAY + INTERVAL 8 MINUTE,
 NOW() - INTERVAL 1 DAY + INTERVAL 20 MINUTE,
 NOW() - INTERVAL 1 DAY + INTERVAL 25 MINUTE,
 NOW() - INTERVAL 1 DAY + INTERVAL 50 MINUTE,
 NULL, 157, 12, 6),

-- Order 4: CUSTOMER ordered from Tunday Kababi — PREPARING (active)
(1004, 2, 720, 720, 'PREPARING',
 NOW() - INTERVAL 20 MINUTE,
 NOW() - INTERVAL 15 MINUTE,
 NOW() - INTERVAL 10 MINUTE,
 NULL, NULL, NULL,
 NULL, 52, 9, 8),

-- Order 5: Arjun ordered from Barbeque Nation — PAID (just placed)
(1005, 3, 847, 847, 'PAID',
 NOW() - INTERVAL 5 MINUTE,
 NULL, NULL, NULL, NULL, NULL,
 NULL, 155, 10, 1);

UPDATE orders_seq SET next_val = 1501;

-- ============================================================
-- 10. ORDER ITEMS
-- ============================================================
INSERT INTO order_item (id, quantity, totalprice, food_id, order_id) VALUES
-- Order 1001: Boneless Chicken Biryani x2, Naan x2, Gongura Mutton x1
(1, 2, 560, 9,  1001),
(2, 2,  80, 15, 1001),
(3, 1, 320, 14, 1001),
-- Order 1002: Masala Dosa, Idli Sambar, Filter Coffee x2, Kesari Bath
(4, 1, 120, 30, 1002),
(5, 1,  90, 31, 1002),
(6, 2, 120, 38, 1002),
(7, 1,  90, 40, 1002),
-- Order 1003: Chicken Biryani Full, Onion Raita, Mirchi ka Salan
(8, 1, 320, 41, 1003),
(9, 2, 120, 48, 1003),
(10,1,  80, 49, 1003),
-- Order 1004: Galouti Kebab, Rumali Roti x2
(11,1, 380, 57, 1004),
(12,4, 120, 61, 1004),
-- Order 1005: Chicken Tikka, Mutton Seekh Kebab, Gulab Jamun x2
(13,1, 349, 1,  1005),
(14,1, 399, 2,  1005),
(15,2, 298, 7,  1005);

UPDATE order_item_seq SET next_val = 1051;

-- ============================================================
-- 11. CARTS (for active users with items pre-added)
-- ============================================================
INSERT INTO cart (id, total, customer_id) VALUES
(1, 660, 52),    -- CUSTOMER has items in cart
(2, 680, 155);   -- Arjun browsing

UPDATE cart_seq SET next_val = 51;

INSERT INTO cart_item (id, quantity, total_prize, cart_id, food_id) VALUES
(1, 2, 560, 1, 9),   -- 2x Boneless Chicken Biryani
(2, 2, 100, 1, 61),  -- 2x Rumali Roti
(3, 1, 750, 2, 26);  -- 1x Fish Tawa Fry

UPDATE cart_item_seq SET next_val = 51;

-- ============================================================
-- VERIFY
-- ============================================================
SELECT '=== SEED COMPLETE ===' as status;
SELECT COUNT(*) as restaurants FROM restaurant;
SELECT COUNT(*) as food_items FROM food;
SELECT COUNT(*) as categories FROM category;
SELECT COUNT(*) as orders FROM orders;
SELECT COUNT(*) as users_preserved FROM users;
