// src/main/java/com/logbook/vessel_management_system/controller/OwnerController.java
package com.logbook.vessel_management_system.controller;

import com.logbook.vessel_management_system.dto.OwnerDto;
import com.logbook.vessel_management_system.service.OwnerService;
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
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
@Tag(name = "Owner Management", description = "APIs for managing ship owners")
public class OwnerController {

    private final OwnerService ownerService;

    @Operation(
        summary = "Get all owners",
        description = "Retrieves a list of all ship owners in the system with their associated ship information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved list of owners",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OwnerDto.class)))
        )
    })
    @GetMapping
    public ResponseEntity<List<OwnerDto>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @Operation(
        summary = "Create a new owner",
        description = "Creates a new ship owner. Note: This endpoint currently uses OwnerDto for both request and response. In production, consider creating a separate CreateOwnerRequest DTO."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Owner successfully created",
            content = @Content(schema = @Schema(implementation = OwnerDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or owner with the same name already exists",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @PostMapping
    public ResponseEntity<OwnerDto> createOwner(
            @Valid @RequestBody 
            @Parameter(description = "Owner creation details", required = true)
            OwnerDto ownerDto) {
        // For simplicity, using OwnerDto directly for request. Could create a CreateOwnerRequest DTO.
        // Ensure OwnerDto has necessary validation annotations if used this way.
        OwnerDto createdOwner = ownerService.createOwner(ownerDto);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Delete an owner",
        description = "Deletes an owner and removes all ownership associations with ships. The ships themselves are not deleted, only the ownership links are removed."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Owner successfully deleted and all ship ownership links removed"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Owner not found",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(
            @PathVariable 
            @Parameter(description = "Unique identifier of the owner", required = true, example = "1")
            Long ownerId) {
        ownerService.deleteOwner(ownerId);
        return ResponseEntity.noContent().build();
    }
}