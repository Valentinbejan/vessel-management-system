package com.logbook.vessel_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class UpdateShipRequest {
    @NotBlank(message = "Ship name cannot be blank")
    private String shipName;

    // IMO number is generally not updatable
    // private String imoNumber;

    private String shipType;
    private Integer shipTonnage;

    private Set<Long> ownerIds; // Allow updating owners
}