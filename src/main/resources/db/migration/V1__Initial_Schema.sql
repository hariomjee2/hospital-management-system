CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Insert initial roles
INSERT INTO roles (name, description) VALUES
('ROLE_PATIENT', 'Patient access'),
('ROLE_DOCTOR', 'Doctor access'),
('ROLE_NURSE', 'Nurse access'),
('ROLE_RECEPTIONIST', 'Receptionist access'),
('ROLE_ADMIN', 'Hospital Administrator');

-- Insert demo admin user (password: 'password')
INSERT INTO users (id, email, password_hash, is_active)
VALUES (
    '00000000-0000-0000-0000-000000000000',
    'admin@hms.com',
    '$2a$10$ChIgnEirsoh5q0No2J12JOEF.RUu.ogpdKmJgT39lZg8wH6U6iX/K',
    true
);

-- Assign admin role to demo user
INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-0000-0000-000000000000', id FROM roles WHERE name = 'ROLE_ADMIN';
