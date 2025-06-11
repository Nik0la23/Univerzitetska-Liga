-- Delete all existing users (Option 1)
DELETE FROM users;

-- Or update existing passwords to be BCrypt encoded (Option 2)
-- The following passwords will be:
-- admin -> admin123
-- For all other users -> password123
UPDATE users 
SET password = CASE 
    WHEN username = 'admin' THEN '$2a$10$rYmTAjVbVAuMkuEjPDuO6.Z.8gG0gtR9MXXgN9QoHJKE3VHG6bXFC'  -- BCrypt for 'admin123'
    ELSE '$2a$10$ZHzagKnz3xqH0YHaseL.8.1QEOz0AzJ6yZxwIBcZBVKZyBEGGvyEO'  -- BCrypt for 'password123'
END; 