-- Passwords are specified in the format: Password<UserLetter>123. Unless specified otherwise.
-- Encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO local_user(email, firstName, lastName, password, username, email_verified)
    VALUES("UserA@junit.com", "UserA-FirstName", "UserA-LastName", "$2a$12$RPvMYa3plv21JuU4tfVn8O.7XS2mjATj8ISfZyNmtX3dW8xzlAINe", "UserA", true),
        ("UserB@junit.com", "UserB-FirstName", "UserB-LastName", "$2a$12$dwE44q9h8KqDEnADAwgpWeQIx8iYXcPPjAdobmCNP79gDfkZ6i.RW", "UserB", false);