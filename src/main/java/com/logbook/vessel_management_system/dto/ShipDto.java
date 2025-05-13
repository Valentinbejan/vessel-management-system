package com.logbook.vessel_management_system.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ShipDto {
    private Long id; // Corresponds to Ship.id
    private String shipName;
    private String imoNumber;
    private String shipType;
    private Integer shipTonnage;
    private Set<Long> ownerIds;
}