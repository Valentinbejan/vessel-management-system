package com.logbook.vessel_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Entity class representing vessel Owners in the system.
 * Implements Builder Pattern (via Lombok) for simplified object construction.
 */

@Entity
@Table(name = "Owner_Table") 
@Getter
@Setter
@NoArgsConstructor
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Owner_Id") 
    private Long ownerId;

    @Column(name = "Owner_name", nullable = false, unique = true) 
    private String ownerName;

    @ManyToMany(mappedBy = "owners", fetch = FetchType.LAZY)
    private Set<Ship> ships = new HashSet<>();

    public Owner(String ownerName) {
        this.ownerName = ownerName;
    }

     /**
     * Observer Pattern: Helper method that maintains bidirectional relationship
     * consistency with the Ship entity. When an Owner adds a Ship, it ensures
     * the Ship knows about this Owner as well.
     */


    // Helper methods 
    public void addShip(Ship ship) {
        this.ships.add(ship);
        ship.getOwners().add(this);
    }

    /**
     * Observer Pattern: Ensures consistent bidirectional relationship
     * when removing a ship from this owner.
     */

    public void removeShip(Ship ship) {
        this.ships.remove(ship);
        ship.getOwners().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return Objects.equals(ownerId, owner.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId);
    }

    @Override
    public String toString() {
        return "Owner{" +
               "ownerId=" + ownerId +
               ", ownerName='" + ownerName + '\'' +
               '}';
    }
}