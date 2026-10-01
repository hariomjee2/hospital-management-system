CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    blood_group VARCHAR(5),
    emergency_contact VARCHAR(20),
    UNIQUE(user_id)
);

CREATE TABLE doctors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL,
    UNIQUE(user_id)
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    scheduled_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'REQUESTED',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert Demo Patient and Doctor users
INSERT INTO users (id, email, password_hash, is_active) VALUES
('11111111-1111-1111-1111-111111111111', 'patient@hms.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGaGnQtrG1j821PgT2g2', true),
('22222222-2222-2222-2222-222222222222', 'doctor@hms.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGaGnQtrG1j821PgT2g2', true);

INSERT INTO user_roles (user_id, role_id)
SELECT '11111111-1111-1111-1111-111111111111', id FROM roles WHERE name = 'ROLE_PATIENT' UNION ALL
SELECT '22222222-2222-2222-2222-222222222222', id FROM roles WHERE name = 'ROLE_DOCTOR';

INSERT INTO patients (user_id, first_name, last_name, blood_group, emergency_contact) VALUES
('11111111-1111-1111-1111-111111111111', 'John', 'Doe', 'O+', '555-0199');

INSERT INTO doctors (user_id, first_name, last_name, specialization, department, consultation_fee) VALUES
('22222222-2222-2222-2222-222222222222', 'Jane', 'Smith', 'Cardiologist', 'Cardiology', 150.00);
