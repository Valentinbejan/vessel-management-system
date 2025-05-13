package com.logbook.vessel_management_system.controller;

import com.logbook.vessel_management_system.dto.OwnerDto;
import com.logbook.vessel_management_system.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public ResponseEntity<List<OwnerDto>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    // Example endpoint to create an owner
    @PostMapping
    public ResponseEntity<OwnerDto> createOwner(@Valid @RequestBody OwnerDto ownerDto) {
        // For simplicity, using OwnerDto directly for request. Could create a CreateOwnerRequest DTO.
        // Ensure OwnerDto has necessary validation annotations if used this way.
        OwnerDto createdOwner = ownerService.createOwner(ownerDto);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    // 6: Delete an owner who owns several ships
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long ownerId) {
        ownerService.deleteOwner(ownerId);
        return ResponseEntity.noContent().build();
    }
}