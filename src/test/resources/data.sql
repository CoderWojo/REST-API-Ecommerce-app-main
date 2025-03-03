-- Passwords are specified in the format: Password<UserLetter>123. Unless specified otherwise.
-- Encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO local_user(email, first_name, last_name, password, username, email_verified)
    VALUES('UserA@junit.com', 'UserA-FirstName', 'UserA-LastName', '$2a$12$appKl1QuJYB5dyL8L/T4v.wmNzsxMMXjFVnDjNoqn.NmvLDhfp8vi', 'UserA', false),  -- Password123
        ('UserB@junit.com', 'UserB-FirstName', 'UserB-LastName', '$2a$12$AVxD3R6QYyDtFBr0ieVVEeKSwuP6Y./AQ9dntj/X9b/Q2cFzrDKmW', 'UserB', false),-- też^
        ('UserC@junit.com', 'UserC-FirstName', 'UserC-LastName', '$2a$12$23BLUlH04dP/hU9gZLMjX.HDX3dnB6xAi3PU6D0ts8QPqkl.aW0Xi', 'UserC', true);-- też takie haslo

INSERT INTO verification_token(token, created_timestamp, expires_at, user_id)
    VALUES('TestTokenA', '2024-03-03 12:00:00', '2030-03-03 12:01:00', 1),    -- aktywny
        ('TestTokenB', '2024-03-02 12:00:00', '2024-03-02 12:01:00', 2)  -- nieaktywny
        -- dla userC nie musimy dodawac