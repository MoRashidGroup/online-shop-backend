-- =====================================================
-- SEED DATA - Admin wa Kwanza
-- =====================================================
-- Email: admin@shop.com
-- Password: admin123 (BCrypt hash)
-- Role: ADMIN
-- =====================================================

INSERT INTO users (full_name, email, password, phone_no, role, enabled, created_at)
VALUES (
    'Super Admin',
    'admin@shop.com',
    '$2a$10$rX8Yz5J5K5L5M5N5O5P5QuWvXyZ0aBcDeFgHiJkLmNoPqRsTuVwXyZ',
    '0700000000',
    'ADMIN',
    1,
    NOW()
);