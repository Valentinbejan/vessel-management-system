package com.logbook.vessel_management_system.controller;

import com.logbook.vessel_management_system.dto.CreateShipRequest;
import com.logbook.vessel_management_system.dto.ShipDto;
import com.logbook.vessel_management_system.dto.UpdateShipRequest;
import com.logbook.vessel_management_system.service.ShipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ships")
@RequiredArgsConstructor
public class ShipController {

    private final ShipService shipService;

    // 1: Get all ships from ship table
    @GetMapping
    public ResponseEntity<List<ShipDto>> getAllShips() {
        return ResponseEntity.ok(shipService.getAllShips());
    }

    // 2: Add new ship
    @PostMapping
    public ResponseEntity<ShipDto> createShip(@Valid @RequestBody CreateShipRequest request) {
        ShipDto createdShip = shipService.createShip(request);
        return new ResponseEntity<>(createdShip, HttpStatus.CREATED);
    }

    // 3: Ship is updated
    @PutMapping("/{shipId}") // Consistent with service and DTO (Ship.id)
    public ResponseEntity<ShipDto> updateShip(@PathVariable Long shipId, @Valid @RequestBody UpdateShipRequest request) {
        ShipDto updatedShip = shipService.updateShip(shipId, request);
        return ResponseEntity.ok(updatedShip);
    }

    // 4: Ship is deleted
    @DeleteMapping("/{shipId}")
    public ResponseEntity<Void> deleteShip(@PathVariable Long shipId) {
        shipService.deleteShip(shipId);
        return ResponseEntity.noContent().build();
    }

    // 5: Get all the details that can be obtained about the ship
    @GetMapping("/{shipId}/details")
    public ResponseEntity<ShipDto> getShipDetails(@PathVariable Long shipId) {
        ShipDto shipDetails = shipService.getShipDetailsById(shipId);
        return ResponseEntity.ok(shipDetails);
    }
}