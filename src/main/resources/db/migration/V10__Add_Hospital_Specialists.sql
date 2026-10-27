-- Seed Comprehensive Roster of Hospital Specialists across Departments

-- 1. Ensure Dr. Jane Smith (Cardiology) exists
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, 'doctor@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doctor@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doctor@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, 'Jane', 'Smith', 'Cardiologist', 'Cardiology', 150.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222222'::uuid);

-- 2. Dr. Robert Chen (Neurology)
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222223'::uuid, 'doc.neuro@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doc.neuro@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doc.neuro@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222223'::uuid, 'Robert', 'Chen', 'Neurologist', 'Neurology', 180.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222223'::uuid);

-- 3. Dr. Sarah Jenkins (Orthopedics)
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222224'::uuid, 'doc.ortho@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doc.ortho@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doc.ortho@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222224'::uuid, 'Sarah', 'Jenkins', 'Orthopedic Surgeon', 'Orthopedics', 160.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222224'::uuid);

-- 4. Dr. Michael Vance (Pediatrics)
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222225'::uuid, 'doc.peds@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doc.peds@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doc.peds@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222225'::uuid, 'Michael', 'Vance', 'Pediatric Specialist', 'Pediatrics', 120.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222225'::uuid);

-- 5. Dr. Emily Watson (Dermatology)
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222226'::uuid, 'doc.derma@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doc.derma@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doc.derma@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222226'::uuid, 'Emily', 'Watson', 'Dermatologist', 'Dermatology', 140.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222226'::uuid);

-- 6. Dr. Alan Patel (Gastroenterology)
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222227'::uuid, 'doc.gastro@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doc.gastro@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doc.gastro@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222227'::uuid, 'Alan', 'Patel', 'Gastroenterologist', 'Gastroenterology', 170.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222227'::uuid);

-- 7. Dr. Lisa Ray (Pulmonology)
INSERT INTO users (id, email, password_hash, is_active)
SELECT '22222222-2222-2222-2222-222222222228'::uuid, 'doc.pulmo@hms.com', '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'doc.pulmo@hms.com');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'doc.pulmo@hms.com' AND r.name = 'ROLE_DOCTOR'
AND NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = u.id AND role_id = r.id);

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee)
SELECT '22222222-2222-2222-2222-222222222228'::uuid, 'Lisa', 'Ray', 'Pulmonologist', 'Pulmonology', 150.00
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE user_id = '22222222-2222-2222-2222-222222222228'::uuid);
