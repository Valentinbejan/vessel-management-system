-- health_check.sql
-- Script to verify database setup and data integrity

\echo '==================== DATABASE HEALTH CHECK ===================='

-- Check table existence
\echo '1. Verifying table structure...'
SELECT 
    schemaname,
    tablename,
    tableowner,
    tablespace
FROM pg_tables 
WHERE schemaname = 'public' 
ORDER BY tablename;

-- Check primary keys and constraints
\echo '2. Verifying constraints...'
SELECT 
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public'
ORDER BY tc.table_name, tc.constraint_type;

-- Check foreign keys
\echo '3. Verifying foreign key relationships...'
SELECT 
    kcu.table_name as child_table,
    kcu.column_name as child_column,
    ccu.table_name as parent_table,
    ccu.column_name as parent_column
FROM information_schema.key_column_usage kcu
JOIN information_schema.constraint_column_usage ccu 
    ON kcu.constraint_name = ccu.constraint_name
WHERE kcu.constraint_name IN (
    SELECT constraint_name 
    FROM information_schema.table_constraints 
    WHERE constraint_type = 'FOREIGN KEY'
    AND table_schema = 'public'
);

-- Check indexes
\echo '4. Verifying indexes...'
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- Check data counts
\echo '5. Verifying data counts...'
SELECT 
    'Owner_Table' as table_name,
    COUNT(*) as record_count
FROM Owner_Table
UNION ALL
SELECT 
    'Ships_Table' as table_name,
    COUNT(*) as record_count
FROM Ships_Table
UNION ALL
SELECT 
    'Ship_Ownership_Link_Table' as table_name,
    COUNT(*) as record_count
FROM Ship_Ownership_Link_Table
UNION ALL
SELECT 
    'Category_Table' as table_name,
    COUNT(*) as record_count
FROM Category_Table;

-- Check for orphaned records
\echo '6. Checking for data integrity issues...'

-- Ships without owners
SELECT 
    s.Id,
    s.Ship_name,
    'No owners' as issue
FROM Ships_Table s
LEFT JOIN Ship_Ownership_Link_Table sol ON s.Id = sol.Ship_Id_FK
WHERE sol.Ship_Id_FK IS NULL;

-- Ships without details
SELECT 
    s.Id,
    s.Ship_name,
    'No details' as issue
FROM Ships_Table s
LEFT JOIN Category_Table c ON s.Id = c.Ship_id
WHERE c.Ship_id IS NULL;

-- Sample queries to verify relationships
\echo '7. Sample relationship queries...'

-- Ships with their owners
SELECT 
    s.Ship_name,
    s.Imo_number,
    o.Owner_name,
    c.Ship_type,
    c.Ship_tonnage
FROM Ships_Table s
JOIN Ship_Ownership_Link_Table sol ON s.Id = sol.Ship_Id_FK
JOIN Owner_Table o ON sol.Owner_Id_FK = o.Owner_Id
LEFT JOIN Category_Table c ON s.Id = c.Ship_id
ORDER BY s.Ship_name, o.Owner_name;

\echo '==================== HEALTH CHECK COMPLETE ===================='