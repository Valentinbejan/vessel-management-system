-- V1__init_schema.sql
-- Script to create the database schema for the Vessel Management System

-- Drop existing tables if they exist (for clean development/testing)
-- WARNING: This will delete all data! 
DROP TABLE IF EXISTS Ship_Ownership_Link_Table CASCADE;
DROP TABLE IF EXISTS Category_Table CASCADE;
DROP TABLE IF EXISTS Ships_Table CASCADE;
DROP TABLE IF EXISTS Owner_Table CASCADE;

-- Create Owner Table
CREATE TABLE Owner_Table (
    Owner_Id BIGSERIAL PRIMARY KEY,
    Owner_name VARCHAR(255) NOT NULL UNIQUE
);

-- Create Ships Table
CREATE TABLE Ships_Table (
    Id BIGSERIAL PRIMARY KEY,
    Ship_name VARCHAR(255) NOT NULL,
    Imo_number VARCHAR(50) NOT NULL UNIQUE
);

-- Create Ship Ownership Link Table (Many-to-Many)
CREATE TABLE Ship_Ownership_Link_Table (
    Ship_Id_FK BIGINT NOT NULL,
    Owner_Id_FK BIGINT NOT NULL,
    PRIMARY KEY (Ship_Id_FK, Owner_Id_FK),
    FOREIGN KEY (Ship_Id_FK) REFERENCES Ships_Table(Id) ON DELETE CASCADE,
    FOREIGN KEY (Owner_Id_FK) REFERENCES Owner_Table(Owner_Id) ON DELETE CASCADE
);

-- Create Category Table (Ship Details)
CREATE TABLE Category_Table (
    Ship_id BIGINT PRIMARY KEY,
    Ship_type VARCHAR(100),
    Ship_tonnage INTEGER,
    FOREIGN KEY (Ship_id) REFERENCES Ships_Table(Id) ON DELETE CASCADE
);

-- Create useful indexes for performance
CREATE INDEX idx_ships_ship_name ON Ships_Table(Ship_name);
CREATE INDEX idx_ships_imo_number ON Ships_Table(Imo_number); -- Already unique, but helps with queries
CREATE INDEX idx_category_ship_type ON Category_Table(Ship_type);
CREATE INDEX idx_ownership_ship_id ON Ship_Ownership_Link_Table(Ship_Id_FK);
CREATE INDEX idx_ownership_owner_id ON Ship_Ownership_Link_Table(Owner_Id_FK);

-- Add table comments
COMMENT ON TABLE Owner_Table IS 'Stores information about vessel owners';
COMMENT ON TABLE Ships_Table IS 'Stores core information about vessels';
COMMENT ON TABLE Ship_Ownership_Link_Table IS 'Many-to-many relationship between ships and owners';
COMMENT ON TABLE Category_Table IS 'Stores additional ship details (type and tonnage)';

-- Add column comments for clarity
COMMENT ON COLUMN Ships_Table.Imo_number IS 'International Maritime Organization number (unique identifier)';
COMMENT ON COLUMN Category_Table.Ship_tonnage IS 'Ship tonnage in gross tons';