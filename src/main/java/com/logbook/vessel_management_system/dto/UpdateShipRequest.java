// src/main/java/com/logbook/vessel_management_system/dto/UpdateShipRequest.java
package com.logbook.vessel_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Request payload for updating an existing ship")
public class UpdateShipRequest {
    
    @NotBlank(message = "Ship name cannot be blank")
    @Schema(description = "Name of the ship", example = "MV Symphony of the Seas - Updated", required = true)
    private String shipName;

    // IMO number is generally not updatable
    // private String imoNumber;

    @Schema(description = "Type/category of the ship", example = "Luxury Cruise Ship")
    private String shipType;
    
    @Schema(description = "Ship tonnage in gross tons", example = "208081")
    private Integer shipTonnage;

    @Schema(description = "Set of owner IDs to associate with this ship. If provided, replaces all existing ownership associations.", example = "[1, 3]")
    private Set<Long> ownerIds; // Allow updating owners
}