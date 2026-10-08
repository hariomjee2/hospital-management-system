-- Update all existing demo users to have the valid BCrypt hash for 'password'
UPDATE users
SET password_hash = '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K';

-- Insert demo doctor user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'doctor@hms.com',
    '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K',
    true
) ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_DOCTOR') FROM users WHERE email = 'doctor@hms.com'
ON CONFLICT DO NOTHING;

-- Insert demo patient user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'patient@hms.com',
    '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K',
    true
) ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_PATIENT') FROM users WHERE email = 'patient@hms.com'
ON CONFLICT DO NOTHING;

-- Insert demo nurse user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'nurse@hms.com',
    '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K',
    true
) ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_NURSE') FROM users WHERE email = 'nurse@hms.com'
ON CONFLICT DO NOTHING;

-- Insert demo receptionist user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'receptionist@hms.com',
    '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K',
    true
) ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_RECEPTIONIST') FROM users WHERE email = 'receptionist@hms.com'
ON CONFLICT DO NOTHING;
