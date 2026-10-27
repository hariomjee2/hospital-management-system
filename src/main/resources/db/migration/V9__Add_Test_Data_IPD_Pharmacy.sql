-- Seed Active IPD Admission for Demo Patient John Doe in ICU-101
INSERT INTO ipd_admissions (id, patient_id, doctor_id, bed_id, admission_notes, status)
SELECT
    '77777777-7777-7777-7777-777777777701'::uuid,
    p.id,
    d.id,
    b.id,
    'Admitted under cardiac surveillance following acute angina observation.',
    'ADMITTED'
FROM patients p, doctors d, beds b
WHERE p.user_id = '11111111-1111-1111-1111-111111111111'::uuid
AND d.user_id = '22222222-2222-2222-2222-222222222222'::uuid
AND b.bed_number = 'ICU-101'
AND NOT EXISTS (SELECT 1 FROM ipd_admissions WHERE id = '77777777-7777-7777-7777-777777777701'::uuid);

-- Update ICU-101 Bed status to OCCUPIED
UPDATE beds
SET status = 'OCCUPIED'
WHERE bed_number = 'ICU-101';

-- Seed Additional Master Pharmacy Inventory
INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Azithromycin 500mg', 'Azithromycin', 'Antibiotics', 'BATCH-AZI-2026', 4, 15, 14.00, '2027-06-30'::date, 'Sun Pharma'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Azithromycin 500mg');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Ibuprofen 400mg', 'Ibuprofen', 'Analgesics', 'BATCH-IBU-2026', 90, 20, 6.00, '2027-10-31'::date, 'Abbott India'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Ibuprofen 400mg');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Insulin Glargine 100IU/ml', 'Insulin Glargine', 'Diabetes', 'BATCH-INS-2026', 8, 20, 45.00, '2027-05-31'::date, 'Sanofi'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Insulin Glargine 100IU/ml');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Ciprofloxacin 500mg', 'Ciprofloxacin', 'Antibiotics', 'BATCH-CIP-2026', 60, 15, 9.50, '2026-11-10'::date, 'Alkem Labs'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Ciprofloxacin 500mg');
