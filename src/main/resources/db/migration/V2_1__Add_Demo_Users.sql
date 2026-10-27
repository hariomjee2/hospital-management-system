-- Update all existing demo users to have the valid BCrypt hash for 'password'
UPDATE users
SET password_hash = '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K';

-- Insert demo doctor user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, 'doctor@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doctor@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doctor@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

-- Insert demo patient user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
SELECT '11111111-1111-1111-1111-111111111111'::uuid, 'patient@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'patient@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'patient@hms.com' AND r.name = 'ROLE_PATIENT'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

-- Insert demo nurse user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
SELECT '33333333-3333-3333-3333-333333333333'::uuid, 'nurse@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'nurse@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'nurse@hms.com' AND r.name = 'ROLE_NURSE'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

-- Insert demo receptionist user if it doesn't exist
INSERT INTO users (id, email, password_hash, is_active)
SELECT '44444444-4444-4444-4444-444444444444'::uuid, 'receptionist@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'receptionist@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'receptionist@hms.com' AND r.name = 'ROLE_RECEPTIONIST'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);
