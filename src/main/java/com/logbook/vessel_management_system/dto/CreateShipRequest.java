// src/main/java/com/logbook/vessel_management_system/dto/CreateShipRequest.java
package com.logbook.vessel_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

/**
 * DTO Pattern: Specialized input DTO for ship creation.
 * Separates creation concerns from general data representation and provides
 * specific validation rules for the creation operation.
 */

@Data
@Schema(description = "Request payload for creating a new ship")
public class CreateShipRequest {
    
    @NotBlank(message = "Ship name cannot be blank")
    @Schema(description = "Name of the ship", example = "MV Symphony of the Seas", required = true)
    private String shipName;

    @NotBlank(message = "IMO number cannot be blank")
    @Size(min = 7, max = 7, message = "IMO number must be 7 digits")
    @Schema(description = "International Maritime Organization number (7 digits)", example = "9744001", required = true)
    private String imoNumber;

    @Schema(description = "Type/category of the ship", example = "Cruise Ship")
    private String shipType;
    
    @Schema(description = "Ship tonnage in gross tons", example = "208081")
    private Integer shipTonnage;

    @NotEmpty(message = "Ship must have at least one owner ID")
    @Schema(description = "Set of owner IDs to associate with this ship", example = "[1, 2]", required = true)
    private Set<Long> ownerIds;
}