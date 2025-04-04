-- Passwords are specified in the format: Password<UserLetter>123. Unless specified otherwise.
-- Encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO local_user(email, first_name, last_name, password, username, email_verified)
    VALUES('UserA@junit.com', 'UserA-FirstName', 'UserA-LastName', '$2a$12$appKl1QuJYB5dyL8L/T4v.wmNzsxMMXjFVnDjNoqn.NmvLDhfp8vi', 'UserA', false),  -- Password123
        ('UserB@junit.com', 'UserB-FirstName', 'UserB-LastName', '$2a$12$AVxD3R6QYyDtFBr0ieVVEeKSwuP6Y./AQ9dntj/X9b/Q2cFzrDKmW', 'UserB', false),-- też^
        ('UserC@junit.com', 'UserC-FirstName', 'UserC-LastName', '$2a$12$23BLUlH04dP/hU9gZLMjX.HDX3dnB6xAi3PU6D0ts8QPqkl.aW0Xi', 'UserC', true);-- też takie haslo

INSERT INTO verification_token(token, created_timestamp, expires_at, user_id)
    VALUES('TestTokenA', '2024-03-03 12:00:00', '2030-03-03 12:01:00', 1),    -- aktywny
        ('TestTokenB', '2024-03-02 12:00:00', '2024-03-02 12:01:00', 2);  -- nieaktywny
        -- dla userC nie musimy dodawac

INSERT INTO address(address_line_1, city, country, user_id)
    VALUES ('123 Tester Hill', 'Testerton', 'England', 1)
    , ('312 Spring Boot', 'Hibernate', 'England', 3); -- id=2

INSERT INTO product (name, short_description, long_description, price)
    VALUES ('Product #1', 'Product one short description.', 'This is a very long description of product #1.', 5.50)
    , ('Product #2', 'Product two short description.', 'This is a very long description of product #2.', 10.56)
    , ('Product #3', 'Product three short description.', 'This is a very long description of product #3.', 2.74)
    , ('Product #4', 'Product four short description.', 'This is a very long description of product #4.', 15.69)
    , ('Product #5', 'Product five short description.', 'This is a very long description of product #5.', 42.59);

INSERT INTO inventory (product_id, quantity)
    VALUES (1, 5)
    , (2, 8)
    , (3, 12)
    , (4, 73)
    , (5, 2);

INSERT INTO web_order (address_id, user_id)
    VALUES (1, 1)
    , (1, 1)
    , (1, 1)
    , (2, 3)
    , (2, 3);

INSERT INTO web_order_quantities (order_id, product_id, quantity)
    VALUES (1, 1, 5)
    , (1, 2, 5)
    , (2, 3, 5)
    , (2, 2, 5)
    , (2, 5, 5)
    , (3, 3, 5)
    , (4, 4, 5)
    , (4, 2, 5)
    , (5, 3, 5)
    , (5, 1, 5);