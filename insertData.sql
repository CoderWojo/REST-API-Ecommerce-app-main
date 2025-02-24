DO $$ 
DECLARE userId1 INT := 8;
DECLARE userId2 INT := 9;
DECLARE product1 INT;
DECLARE product2 INT;
DECLARE product3 INT;
DECLARE product4 INT;
DECLARE product5 INT;
DECLARE address1 INT;
DECLARE address2 INT;
DECLARE order1 INT;
DECLARE order2 INT;
DECLARE order3 INT;
DECLARE order4 INT;
DECLARE order5 INT;
BEGIN

-- Usunięcie danych
TRUNCATE TABLE web_order_quantities, web_order, inventory, product, address RESTART IDENTITY CASCADE;

-- Dodanie produktów
INSERT INTO product (name, short_desc, long_desc, price) VALUES 
('Product #1', 'Product one short description.', 'This is a very long description of product #1.', 5.50),
('Product #2', 'Product two short description.', 'This is a very long description of product #2.', 10.56),
('Product #3', 'Product three short description.', 'This is a very long description of product #3.', 2.74),
('Product #4', 'Product four short description.', 'This is a very long description of product #4.', 15.69),
('Product #5', 'Product five short description.', 'This is a very long description of product #5.', 42.59);

-- Pobranie ID produktów
SELECT id INTO product1 FROM product WHERE name = 'Product #1' LIMIT 1;
SELECT id INTO product2 FROM product WHERE name = 'Product #2' LIMIT 1;
SELECT id INTO product3 FROM product WHERE name = 'Product #3' LIMIT 1;
SELECT id INTO product4 FROM product WHERE name = 'Product #4' LIMIT 1;
SELECT id INTO product5 FROM product WHERE name = 'Product #5' LIMIT 1;

-- Dodanie stanów magazynowych
INSERT INTO inventory (product_id, quantity) VALUES 
(product1, 5), (product2, 8), (product3, 12), (product4, 73), (product5, 2);

-- Dodanie adresów
INSERT INTO address (address_line_1, city, country, user_id) VALUES 
('123 Tester Hill', 'Testerton', 'England', userId1),
('312 Spring Boot', 'Hibernate', 'England', userId2);

-- Pobranie ID adresów
SELECT id INTO address1 FROM address WHERE user_id = userId1 ORDER BY id DESC LIMIT 1;
SELECT id INTO address2 FROM address WHERE user_id = userId2 ORDER BY id DESC LIMIT 1;

-- Dodanie zamówień
INSERT INTO web_order (address_id, user_id) VALUES (address1, userId1), (address1, userId1), (address1, userId1), (address2, userId2), (address2, userId2);

-- Pobranie ID zamówień
SELECT id INTO order1 FROM web_order WHERE address_id = address1 AND user_id = userId1 ORDER BY id DESC LIMIT 1;
SELECT id INTO order2 FROM web_order WHERE address_id = address1 AND user_id = userId1 ORDER BY id DESC LIMIT 1 OFFSET 1;
SELECT id INTO order3 FROM web_order WHERE address_id = address1 AND user_id = userId1 ORDER BY id DESC LIMIT 1 OFFSET 2;
SELECT id INTO order4 FROM web_order WHERE address_id = address2 AND user_id = userId2 ORDER BY id DESC LIMIT 1;
SELECT id INTO order5 FROM web_order WHERE address_id = address2 AND user_id = userId2 ORDER BY id DESC LIMIT 1 OFFSET 1;

-- Dodanie ilości produktów w zamówieniach
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES 
(order1, product1, 5), (order1, product2, 5), 
(order2, product3, 5), (order2, product2, 5), (order2, product5, 5),
(order3, product3, 5), (order4, product4, 5), (order4, product2, 5),
(order5, product3, 5), (order5, product1, 5);

END $$;
