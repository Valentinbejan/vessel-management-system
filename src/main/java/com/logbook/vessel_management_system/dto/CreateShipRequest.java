package com.logbook.vessel_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
// import jakarta.validation.constraints.NotNull; // Removed unused import
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class CreateShipRequest {
    @NotBlank(message = "Ship name cannot be blank")
    private String shipName;

    @NotBlank(message = "IMO number cannot be blank")
    @Size(min = 7, max = 7, message = "IMO number must be 7 digits")
    private String imoNumber;

    private String shipType;
    private Integer shipTonnage;

    @NotEmpty(message = "Ship must have at least one owner ID")
    private Set<Long> ownerIds;
}