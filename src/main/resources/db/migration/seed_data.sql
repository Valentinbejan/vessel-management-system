-- seed_data.sql
-- Inserts sample data for the Vessel Management System
-- Run this script after creating the schema

-- Insert Owners
INSERT INTO Owner_Table (Owner_name) VALUES
    ('Royal Caribbean Cruises'),
    ('Carnival Cruises'),
    ('Mitsubishi Heavy Industries'),
    ('Mitsui O.S.K. Lines'),
    ('Holland America Cruises')
ON CONFLICT (Owner_name) DO NOTHING;

-- Insert Ships
INSERT INTO Ships_Table (Ship_name, Imo_number) VALUES
    ('Symphony of the Seas', '9744001'),
    ('Eco Arctic', '9746683'),
    ('Explorer Spirit', '9313486'),
    ('Carnival Luminosa', '9398905')
ON CONFLICT (Imo_number) DO NOTHING;

-- Insert Ship Categories (Details)
DO $do$
DECLARE
    symphony_id BIGINT;
    eco_arctic_id BIGINT;
    explorer_spirit_id BIGINT;
    carnival_luminosa_id BIGINT;
BEGIN
    -- Get ship IDs by IMO number
    SELECT Id INTO symphony_id FROM Ships_Table WHERE Imo_number = '9744001';
    SELECT Id INTO eco_arctic_id FROM Ships_Table WHERE Imo_number = '9746683';
    SELECT Id INTO explorer_spirit_id FROM Ships_Table WHERE Imo_number = '9313486';
    SELECT Id INTO carnival_luminosa_id FROM Ships_Table WHERE Imo_number = '9398905';

    -- Insert category details
    IF symphony_id IS NOT NULL THEN
        INSERT INTO Category_Table (Ship_id, Ship_type, Ship_tonnage)
        VALUES (symphony_id, 'Cruise', 208081) 
        ON CONFLICT (Ship_id) DO NOTHING;
    END IF;

    IF eco_arctic_id IS NOT NULL THEN
        INSERT INTO Category_Table (Ship_id, Ship_type, Ship_tonnage)
        VALUES (eco_arctic_id, 'Crude Oil Tanker', 19554) 
        ON CONFLICT (Ship_id) DO NOTHING;
    END IF;

    IF explorer_spirit_id IS NOT NULL THEN
        INSERT INTO Category_Table (Ship_id, Ship_type, Ship_tonnage)
        VALUES (explorer_spirit_id, 'LPG Tanker', 57657) 
        ON CONFLICT (Ship_id) DO NOTHING;
    END IF;

    IF carnival_luminosa_id IS NOT NULL THEN
        INSERT INTO Category_Table (Ship_id, Ship_type, Ship_tonnage)
        VALUES (carnival_luminosa_id, 'Cruise', 323872) 
        ON CONFLICT (Ship_id) DO NOTHING;
    END IF;
END $do$;

-- Link Ships to Owners
DO $do$
DECLARE
    symphony_id BIGINT;
    eco_arctic_id BIGINT;
    explorer_spirit_id BIGINT;
    carnival_luminosa_id BIGINT;
    royal_owner_id BIGINT;
    mitsubishi_owner_id BIGINT;
    mitsui_owner_id BIGINT;
    carnival_owner_id BIGINT;
BEGIN
    -- Get ship IDs
    SELECT Id INTO symphony_id FROM Ships_Table WHERE Imo_number = '9744001';
    SELECT Id INTO eco_arctic_id FROM Ships_Table WHERE Imo_number = '9746683';
    SELECT Id INTO explorer_spirit_id FROM Ships_Table WHERE Imo_number = '9313486';
    SELECT Id INTO carnival_luminosa_id FROM Ships_Table WHERE Imo_number = '9398905';

    -- Get owner IDs
    SELECT Owner_Id INTO royal_owner_id FROM Owner_Table WHERE Owner_name = 'Royal Caribbean Cruises';
    SELECT Owner_Id INTO mitsubishi_owner_id FROM Owner_Table WHERE Owner_name = 'Mitsubishi Heavy Industries';
    SELECT Owner_Id INTO mitsui_owner_id FROM Owner_Table WHERE Owner_name = 'Mitsui O.S.K. Lines';
    SELECT Owner_Id INTO carnival_owner_id FROM Owner_Table WHERE Owner_name = 'Carnival Cruises';

    -- Create ownership links
    IF symphony_id IS NOT NULL AND royal_owner_id IS NOT NULL THEN
        INSERT INTO Ship_Ownership_Link_Table (Ship_Id_FK, Owner_Id_FK)
        VALUES (symphony_id, royal_owner_id) 
        ON CONFLICT (Ship_Id_FK, Owner_Id_FK) DO NOTHING;
    END IF;

    IF eco_arctic_id IS NOT NULL AND mitsubishi_owner_id IS NOT NULL THEN
        INSERT INTO Ship_Ownership_Link_Table (Ship_Id_FK, Owner_Id_FK)
        VALUES (eco_arctic_id, mitsubishi_owner_id) 
        ON CONFLICT (Ship_Id_FK, Owner_Id_FK) DO NOTHING;
    END IF;

    IF explorer_spirit_id IS NOT NULL AND mitsui_owner_id IS NOT NULL THEN
        INSERT INTO Ship_Ownership_Link_Table (Ship_Id_FK, Owner_Id_FK)
        VALUES (explorer_spirit_id, mitsui_owner_id) 
        ON CONFLICT (Ship_Id_FK, Owner_Id_FK) DO NOTHING;
    END IF;

    IF carnival_luminosa_id IS NOT NULL AND carnival_owner_id IS NOT NULL THEN
        INSERT INTO Ship_Ownership_Link_Table (Ship_Id_FK, Owner_Id_FK)
        VALUES (carnival_luminosa_id, carnival_owner_id) 
        ON CONFLICT (Ship_Id_FK, Owner_Id_FK) DO NOTHING;
    END IF;

    -- Add some examples of ships with multiple owners
    IF symphony_id IS NOT NULL AND mitsubishi_owner_id IS NOT NULL THEN
        INSERT INTO Ship_Ownership_Link_Table (Ship_Id_FK, Owner_Id_FK)
        VALUES (symphony_id, mitsubishi_owner_id) 
        ON CONFLICT (Ship_Id_FK, Owner_Id_FK) DO NOTHING;
    END IF;
END $do$;

-- Verify data insertion
\echo 'Seed data insertion complete!'
\echo 'Verifying data...'

SELECT 
    COUNT(*) as owner_count,
    'owners' as table_name
FROM Owner_Table
UNION ALL
SELECT 
    COUNT(*) as ship_count,
    'ships' as table_name
FROM Ships_Table
UNION ALL
SELECT 
    COUNT(*) as ownership_links,
    'ownership links' as table_name
FROM Ship_Ownership_Link_Table
UNION ALL
SELECT 
    COUNT(*) as ship_details,
    'ship details' as table_name
FROM Category_Table;