// src/main/java/com/logbook/vessel_management_system/dto/ShipDto.java
package com.logbook.vessel_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Set;

/**
 * DTO Pattern: Data Transfer Object specific to Ship entities.
 * Separates the domain model from the presentation layer and provides a flat
 * structure for API responses, hiding the complexity of entity relationships.
 */

@Data
@Schema(description = "Ship data transfer object containing complete ship information")
public class ShipDto {
    
    @Schema(description = "Unique identifier of the ship", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id; // Corresponds to Ship.id
    
    @Schema(description = "Name of the ship", example = "MV Symphony of the Seas")
    private String shipName;
    
    @Schema(description = "International Maritime Organization number", example = "9744001")
    private String imoNumber;
    
    @Schema(description = "Type/category of the ship", example = "Cruise Ship")
    private String shipType;
    
    @Schema(description = "Ship tonnage in gross tons", example = "208081")
    private Integer shipTonnage;
    
    @Schema(description = "Set of owner IDs associated with this ship", example = "[1, 2]")
    private Set<Long> ownerIds;
}