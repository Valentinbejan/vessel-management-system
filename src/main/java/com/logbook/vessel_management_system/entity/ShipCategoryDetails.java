package com.logbook.vessel_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Entity
@Table(name = "Category_Table") // Matches your SQL table name
@Getter
@Setter
@NoArgsConstructor
public class ShipCategoryDetails {

    @Id // This ID will be the same as the Ship's ID
    @Column(name = "Ship_id") // Matches your SQL column name
    private Long shipId; // This name makes sense as it's the ship's ID

    @Column(name = "Ship_type") // Matches your SQL column name
    private String shipType;

    @Column(name = "Ship_tonnage") // Matches your SQL column name
    private Integer shipTonnage;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Maps the shipId field to be both the PK and the FK to Ship
    @JoinColumn(name = "Ship_id") // Specifies the FK column (same as PK here)
    private Ship ship;

    public ShipCategoryDetails(String shipType, Integer shipTonnage) {
        this.shipType = shipType;
        this.shipTonnage = shipTonnage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipCategoryDetails that = (ShipCategoryDetails) o;
        return Objects.equals(shipId, that.shipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipId);
    }

    @Override
    public String toString() {
        return "ShipCategoryDetails{" +
               "shipId=" + shipId +
               ", shipType='" + shipType + '\'' +
               ", shipTonnage=" + shipTonnage +
               '}';
    }
}