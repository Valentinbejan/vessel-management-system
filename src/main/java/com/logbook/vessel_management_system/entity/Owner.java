package com.logbook.vessel_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "Owner_Table") // Matches your SQL table name
@Getter
@Setter
@NoArgsConstructor
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Owner_Id") // Matches your SQL column name
    private Long ownerId;

    @Column(name = "Owner_name", nullable = false, unique = true) // Matches your SQL column name
    private String ownerName;

    @ManyToMany(mappedBy = "owners", fetch = FetchType.LAZY)
    private Set<Ship> ships = new HashSet<>();

    public Owner(String ownerName) {
        this.ownerName = ownerName;
    }

    // Helper methods (optional but good practice)
    public void addShip(Ship ship) {
        this.ships.add(ship);
        ship.getOwners().add(this);
    }

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