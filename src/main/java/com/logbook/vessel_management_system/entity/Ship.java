package com.logbook.vessel_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Entity class representing Ships in the system.
 * Multiple patterns are applied here:
 * 1. Domain Model Pattern - Encapsulates data and behavior related to ships
 * 2. Builder Pattern (via Lombok) - Simplifies object construction
 * 3. Singleton Pattern (via Spring) - Entity instances managed by JPA
 */

@Entity
@Table(name = "Ships_Table") // Matches your SQL table name
@Getter
@Setter
@NoArgsConstructor
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") 
    private Long id; 

    @Column(name = "Ship_name", nullable = false) 
    private String shipName;

    @Column(name = "Imo_number", nullable = false, unique = true) 
    private String imoNumber;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "Ship_Ownership_Link_Table", 
        joinColumns = @JoinColumn(name = "Ship_Id_FK"), 
        inverseJoinColumns = @JoinColumn(name = "Owner_Id_FK")
    )
    private Set<Owner> owners = new HashSet<>();

    @OneToOne(mappedBy = "ship", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private ShipCategoryDetails details; 

    public Ship(String shipName, String imoNumber) {
        this.shipName = shipName;
        this.imoNumber = imoNumber;
    }

    /**
     * Observer Pattern: These helper methods ensure bidirectional relationship
     * consistency. When a Ship adds an Owner, it also ensures the Owner
     * knows about this Ship, propagating changes across the object graph.
     */

    // Helper methods for relationship management
    public void addOwner(Owner owner) {
        this.owners.add(owner);
        owner.getShips().add(this);
    }

    /**
     * Observer Pattern: Similar to addOwner, this maintains bidirectional
     * relationship consistency when removing an ownership relationship.
     */

    public void removeOwner(Owner owner) {
        this.owners.remove(owner);
        owner.getShips().remove(this);
    }

    /**
     * Observer Pattern: Ensures changes to the Ship's details are reflected
     * in the ShipCategoryDetails entity, maintaining consistency.
     */

    public void setDetails(ShipCategoryDetails details) {
        if (details == null) {
            if (this.details != null) {
                this.details.setShip(null);
            }
        } else {
            details.setShip(this);
        }
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return Objects.equals(id, ship.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

     @Override
    public String toString() {
        return "Ship{" +
               "id=" + id +
               ", shipName='" + shipName + '\'' +
               ", imoNumber='" + imoNumber + '\'' +
               '}';
    }
}