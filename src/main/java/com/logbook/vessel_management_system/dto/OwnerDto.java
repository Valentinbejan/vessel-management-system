// src/main/java/com/logbook/vessel_management_system/dto/OwnerDto.java
package com.logbook.vessel_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Owner data transfer object containing owner information and associated ships")
public class OwnerDto {
    
    @Schema(description = "Unique identifier of the owner", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long ownerId;
    
    @NotBlank(message = "Owner name cannot be blank")
    @Schema(description = "Name of the ship owner", example = "Royal Caribbean Cruises", required = true)
    private String ownerName;
    
    @Schema(description = "Set of ship IDs owned by this owner", example = "[1, 2, 3]")
    private Set<Long> shipIds; // IDs of ships owned by this owner
}