CREATE TABLE IF NOT EXISTS pharmacy_medicines (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    medicine_name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    category VARCHAR(100) NOT NULL,
    batch_number VARCHAR(100) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 10,
    unit_price DECIMAL(10, 2) NOT NULL,
    expiry_date DATE NOT NULL,
    manufacturer VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pharmacy_dispensings (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    patient_id UUID REFERENCES patients(id) ON DELETE SET NULL,
    prescription_id UUID REFERENCES prescriptions(id) ON DELETE SET NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'UNPAID',
    invoice_id UUID REFERENCES invoices(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pharmacy_dispensing_items (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    dispensing_id UUID NOT NULL REFERENCES pharmacy_dispensings(id) ON DELETE CASCADE,
    medicine_id UUID NOT NULL REFERENCES pharmacy_medicines(id) ON DELETE CASCADE,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL
);

-- Seed Production Pharmacy Medicine Inventory
INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Paracetamol 500mg', 'Acetaminophen', 'Analgesics', 'BATCH-PARA-2026', 150, 20, 5.00, '2027-12-31'::date, 'Sun Pharma'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Paracetamol 500mg');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Amoxicillin 250mg', 'Amoxicillin Trihydrate', 'Antibiotics', 'BATCH-AMOX-2026', 80, 15, 12.50, '2027-06-30'::date, 'Cipla Ltd'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Amoxicillin 250mg');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Metformin 500mg', 'Metformin Hydrochloride', 'Diabetes', 'BATCH-MET-2026', 120, 25, 8.00, '2027-08-31'::date, 'Lupin Labs'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Metformin 500mg');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Atorvastatin 10mg', 'Atorvastatin Calcium', 'Cardiac', 'BATCH-ATOR-2026', 5, 20, 18.00, '2027-05-31'::date, 'Torrent Pharma'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Atorvastatin 10mg');

INSERT INTO pharmacy_medicines (medicine_name, generic_name, category, batch_number, stock_quantity, reorder_level, unit_price, expiry_date, manufacturer)
SELECT 'Omeprazole 20mg', 'Omeprazole', 'Gastrointestinal', 'BATCH-OMEP-2026', 200, 30, 6.50, '2026-11-15'::date, 'Dr. Reddys Labs'
WHERE NOT EXISTS (SELECT 1 FROM pharmacy_medicines WHERE medicine_name = 'Omeprazole 20mg');
