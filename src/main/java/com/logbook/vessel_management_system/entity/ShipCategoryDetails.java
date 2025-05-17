package com.logbook.vessel_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Entity
@Table(name = "Category_Table") 
@Getter
@Setter
@NoArgsConstructor
public class ShipCategoryDetails {

    @Id 
    @Column(name = "Ship_id") 
    private Long shipId; 

    @Column(name = "Ship_type") 
    private String shipType;

    @Column(name = "Ship_tonnage") 
    private Integer shipTonnage;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId 
    @JoinColumn(name = "Ship_id") 
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