CREATE TABLE IF NOT EXISTS wards (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    ward_name VARCHAR(100) NOT NULL UNIQUE,
    ward_type VARCHAR(50) NOT NULL,
    daily_rate DECIMAL(10, 2) NOT NULL,
    total_beds INT NOT NULL DEFAULT 10,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS beds (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    ward_id UUID NOT NULL REFERENCES wards(id) ON DELETE CASCADE,
    bed_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE(ward_id, bed_number)
);

CREATE TABLE IF NOT EXISTS ipd_admissions (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    bed_id UUID NOT NULL REFERENCES beds(id),
    admission_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    discharge_date TIMESTAMP WITH TIME ZONE,
    admission_notes TEXT,
    discharge_summary TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ADMITTED',
    total_bill_amount DECIMAL(10, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed Wards & Beds
INSERT INTO wards (id, ward_name, ward_type, daily_rate, total_beds)
SELECT '55555555-5555-5555-5555-555555555501'::uuid, 'Intensive Care Unit (ICU)', 'ICU', 250.00, 6
WHERE NOT EXISTS (SELECT 1 FROM wards WHERE ward_name = 'Intensive Care Unit (ICU)');

INSERT INTO wards (id, ward_name, ward_type, daily_rate, total_beds)
SELECT '55555555-5555-5555-5555-555555555502'::uuid, 'General Medical Ward', 'GENERAL', 75.00, 10
WHERE NOT EXISTS (SELECT 1 FROM wards WHERE ward_name = 'General Medical Ward');

INSERT INTO wards (id, ward_name, ward_type, daily_rate, total_beds)
SELECT '55555555-5555-5555-5555-555555555503'::uuid, 'Private Deluxe Suite', 'PRIVATE', 180.00, 4
WHERE NOT EXISTS (SELECT 1 FROM wards WHERE ward_name = 'Private Deluxe Suite');

-- Seed ICU Beds
INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555501'::uuid, 'ICU-101', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'ICU-101');

INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555501'::uuid, 'ICU-102', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'ICU-102');

INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555501'::uuid, 'ICU-103', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'ICU-103');

-- Seed General Ward Beds
INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555502'::uuid, 'GEN-201', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'GEN-201');

INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555502'::uuid, 'GEN-202', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'GEN-202');

INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555502'::uuid, 'GEN-203', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'GEN-203');

-- Seed Private Suite Beds
INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555503'::uuid, 'PVT-301', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'PVT-301');

INSERT INTO beds (ward_id, bed_number, status)
SELECT '55555555-5555-5555-5555-555555555503'::uuid, 'PVT-302', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM beds WHERE bed_number = 'PVT-302');
