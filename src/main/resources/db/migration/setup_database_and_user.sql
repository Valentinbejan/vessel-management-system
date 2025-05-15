-- setup_database_and_user.sql
-- Run this script as PostgreSQL superuser (e.g., 'postgres')

-- Configuration (change these values as needed)
\set app_user 'vms_app_user'
\set app_database 'vessel_management_system'

-- 1. Create application user (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM pg_catalog.pg_roles 
        WHERE rolname = 'vms_app_user'
    ) THEN
        -- IMPORTANT: Change 'your_secure_password' to a strong password
        CREATE USER vms_app_user WITH PASSWORD 'your_secure_password';
        RAISE NOTICE 'User vms_app_user created successfully';
    ELSE
        RAISE NOTICE 'User vms_app_user already exists, skipping creation';
    END IF;
END $$;

-- 2. Create database (if not exists)
SELECT pg_catalog.pg_database.datname 
FROM pg_catalog.pg_database 
WHERE pg_catalog.pg_database.datname = 'vessel_management_system'
\gset

-- If database doesn't exist, create it
-- Note: This needs to be run outside a transaction block
\if :{?datname}
    \echo 'Database vessel_management_system already exists'
\else
    \echo 'Creating database vessel_management_system...'
    CREATE DATABASE vessel_management_system OWNER vms_app_user;
\endif

-- 3. Connect to the application database and grant privileges
\c vessel_management_system

-- Grant connection privileges
GRANT CONNECT ON DATABASE vessel_management_system TO vms_app_user;

-- Grant schema privileges
GRANT USAGE ON SCHEMA public TO vms_app_user;
GRANT CREATE ON SCHEMA public TO vms_app_user;

-- Grant privileges on existing tables and sequences
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO vms_app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO vms_app_user;

-- Grant privileges on future tables and sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vms_app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public 
GRANT USAGE, SELECT ON SEQUENCES TO vms_app_user;

\echo '==================================================================================='
\echo 'Database setup complete!'
\echo 'Database: vessel_management_system'
\echo 'User: vms_app_user'
\echo 'Next steps:'
\echo '1. Update your application.properties with the new password'
\echo '2. Run the schema creation script as vms_app_user'
\echo '3. Optionally run the seed data script'
\echo '==================================================================================='