CREATE TABLE IF NOT EXISTS lab_test_catalog (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    test_name VARCHAR(255) NOT NULL,
    disease_category VARCHAR(100) NOT NULL,
    test_code VARCHAR(50) UNIQUE NOT NULL,
    sample_required VARCHAR(100) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS patient_lab_orders (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    appointment_id UUID REFERENCES appointments(id) ON DELETE SET NULL,
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id UUID NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    test_id UUID NOT NULL REFERENCES lab_test_catalog(id) ON DELETE CASCADE,
    disease_condition VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ORDERED',
    doctor_notes TEXT,
    result_summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed Production Master Lab Test Catalog
INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Lipid Profile (Cholesterol, HDL, LDL, Triglycerides)', 'Cardiology', 'LAB_LIPID_01', 'Blood', 45.00, 'Assesses cardiovascular risk and lipid levels.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_LIPID_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Electrocardiogram (12-Lead ECG)', 'Cardiology', 'LAB_ECG_01', 'ECG', 60.00, 'Records electrical activity of the heart.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_ECG_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Troponin-I Cardiac Biomarker', 'Cardiology', 'LAB_TROP_01', 'Blood', 80.00, 'Detects acute myocardial injury or cardiac stress.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_TROP_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'HbA1c Glycated Hemoglobin', 'Diabetes', 'LAB_HBA1C_01', 'Blood', 35.00, 'Measures average blood sugar levels over past 3 months.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_HBA1C_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Fasting Blood Sugar (FBS)', 'Diabetes', 'LAB_FBS_01', 'Blood', 20.00, 'Evaluates fasting glucose for diabetes management.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_FBS_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Thyroid Panel (T3, T4, TSH)', 'Thyroid', 'LAB_THY_01', 'Blood', 50.00, 'Evaluates thyroid gland function.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_THY_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Complete Blood Count (CBC)', 'Infection', 'LAB_CBC_01', 'Blood', 25.00, 'Screens for anemia, infection, and blood disorders.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_CBC_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'C-Reactive Protein (CRP)', 'Infection', 'LAB_CRP_01', 'Blood', 30.00, 'Measures systemic inflammation.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_CRP_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Urine Routine & Microscopic Culture', 'Infection', 'LAB_URINE_01', 'Urine', 25.00, 'Detects urinary tract infections or kidney issues.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_URINE_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Renal Function Test (KFT - Creatinine, Urea, BUN)', 'Renal', 'LAB_KFT_01', 'Blood', 40.00, 'Evaluates kidney filtration performance.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_KFT_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Serum Electrolytes (Na+, K+, Cl-)', 'Renal', 'LAB_ELEC_01', 'Blood', 30.00, 'Measures electrolyte balance.'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_ELEC_01');

INSERT INTO lab_test_catalog (test_name, disease_category, test_code, sample_required, cost, description)
SELECT 'Liver Function Test (LFT - SGOT, SGPT, Bilirubin)', 'Hepatic', 'LAB_LFT_01', 'Blood', 45.00, 'Evaluates liver health and enzyme levels'
WHERE NOT EXISTS (SELECT 1 FROM lab_test_catalog WHERE test_code = 'LAB_LFT_01');
