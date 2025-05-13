-- V1__init_schema.sql 

CREATE TABLE Owner_Table (
    Owner_Id BIGSERIAL PRIMARY KEY,
    Owner_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Ships_Table (
    Id BIGSERIAL PRIMARY KEY, -- This is the ship's unique ID
    Ship_name VARCHAR(255) NOT NULL,
    Imo_number VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Ship_Ownership_Link_Table (
    Ship_Id_FK BIGINT NOT NULL,
    Owner_Id_FK BIGINT NOT NULL,
    PRIMARY KEY (Ship_Id_FK, Owner_Id_FK),
    FOREIGN KEY (Ship_Id_FK) REFERENCES Ships_Table(Id) ON DELETE CASCADE,
    FOREIGN KEY (Owner_Id_FK) REFERENCES Owner_Table(Owner_Id) ON DELETE CASCADE
);

CREATE TABLE Category_Table ( -- Storing Ship_type and Ship_tonnage
    Ship_id BIGINT PRIMARY KEY, -- Ship_id is both PK and FK to Ships_Table.Id
    Ship_type VARCHAR(100),
    Ship_tonnage INTEGER,
    FOREIGN KEY (Ship_id) REFERENCES Ships_Table(Id) ON DELETE CASCADE
);