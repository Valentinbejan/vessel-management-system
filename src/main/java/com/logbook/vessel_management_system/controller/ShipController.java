// src/main/java/com/logbook/vessel_management_system/controller/ShipController.java
package com.logbook.vessel_management_system.controller;

import com.logbook.vessel_management_system.dto.CreateShipRequest;
import com.logbook.vessel_management_system.dto.ShipDto;
import com.logbook.vessel_management_system.dto.UpdateShipRequest;
import com.logbook.vessel_management_system.service.ShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ships")
@RequiredArgsConstructor
@Tag(name = "Ship Management", description = "APIs for managing maritime vessels")
public class ShipController {

    private final ShipService shipService;

    @Operation(
        summary = "Get all ships",
        description = "Retrieves a list of all ships in the system with basic information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved list of ships",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShipDto.class)))
        )
    })
    @GetMapping
    public ResponseEntity<List<ShipDto>> getAllShips() {
        return ResponseEntity.ok(shipService.getAllShips());
    }

    @Operation(
        summary = "Get ship by ID",
        description = "Retrieves comprehensive information about a specific ship including category details and associated owners"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved ship details",
            content = @Content(schema = @Schema(implementation = ShipDto.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Ship not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @GetMapping("/{shipId}")
    public ResponseEntity<ShipDto> getShipById(
            @PathVariable 
            @Parameter(description = "Unique identifier of the ship", required = true, example = "1")
            Long shipId) {
        ShipDto ship = shipService.getShipById(shipId);
        return ResponseEntity.ok(ship);
    }

    @Operation(
        summary = "Create a new ship",
        description = "Creates a new ship with the provided details and associates it with existing owners"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Ship successfully created",
            content = @Content(schema = @Schema(implementation = ShipDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or validation errors",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "One or more owner IDs not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @PostMapping
    public ResponseEntity<ShipDto> createShip(
            @Valid @RequestBody 
            @Parameter(description = "Ship creation details", required = true)
            CreateShipRequest request) {
        ShipDto createdShip = shipService.createShip(request);
        return new ResponseEntity<>(createdShip, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update an existing ship",
        description = "Updates ship details and owner associations for an existing ship"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Ship successfully updated",
            content = @Content(schema = @Schema(implementation = ShipDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or validation errors",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Ship not found or one or more owner IDs not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @PutMapping("/{shipId}")
    public ResponseEntity<ShipDto> updateShip(
            @PathVariable 
            @Parameter(description = "Unique identifier of the ship", required = true, example = "1")
            Long shipId, 
            @Valid @RequestBody 
            @Parameter(description = "Ship update details", required = true)
            UpdateShipRequest request) {
        ShipDto updatedShip = shipService.updateShip(shipId, request);
        return ResponseEntity.ok(updatedShip);
    }

    @Operation(
        summary = "Delete a ship",
        description = "Deletes a ship and all its related data (category details, ownership links)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Ship successfully deleted"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Ship not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @DeleteMapping("/{shipId}")
    public ResponseEntity<Void> deleteShip(
            @PathVariable 
            @Parameter(description = "Unique identifier of the ship", required = true, example = "1")
            Long shipId) {
        shipService.deleteShip(shipId);
        return ResponseEntity.noContent().build();
    }
}