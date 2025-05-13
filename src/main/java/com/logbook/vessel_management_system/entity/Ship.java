package com.logbook.vessel_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "Ships_Table") // Matches your SQL table name
@Getter
@Setter
@NoArgsConstructor
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") // Matches your SQL column name for Ship's PK
    private Long id; // Consider renaming to shipId for clarity in Java, but 'id' matches table

    @Column(name = "Ship_name", nullable = false) // Matches your SQL column name
    private String shipName;

    @Column(name = "Imo_number", nullable = false, unique = true) // Matches your SQL column name
    private String imoNumber;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "Ship_Ownership_Link_Table", // Matches your join table name
        joinColumns = @JoinColumn(name = "Ship_Id_FK"), // FK in join table referencing Ship
        inverseJoinColumns = @JoinColumn(name = "Owner_Id_FK") // FK in join table referencing Owner
    )
    private Set<Owner> owners = new HashSet<>();

    @OneToOne(mappedBy = "ship", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private ShipCategoryDetails details; // Renamed from ShipDetails to match Category_Table intent

    public Ship(String shipName, String imoNumber) {
        this.shipName = shipName;
        this.imoNumber = imoNumber;
    }

    // Helper methods for relationship management
    public void addOwner(Owner owner) {
        this.owners.add(owner);
        owner.getShips().add(this);
    }

    public void removeOwner(Owner owner) {
        this.owners.remove(owner);
        owner.getShips().remove(this);
    }

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