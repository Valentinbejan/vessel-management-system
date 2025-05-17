// src/main/java/com/logbook/vessel_management_system/service/ShipService.java
package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.CreateShipRequest;
import com.logbook.vessel_management_system.dto.ShipDto;
import com.logbook.vessel_management_system.dto.UpdateShipRequest;

import java.util.List;

/**
 * Facade Pattern: Provides a simplified interface for ship management operations,
 * hiding the complexity of repositories and entity relationships.
 */

public interface ShipService {
    List<ShipDto> getAllShips();
    ShipDto getShipById(Long shipId);
    ShipDto createShip(CreateShipRequest request);
    ShipDto updateShip(Long shipId, UpdateShipRequest request);
    void deleteShip(Long shipId);
}