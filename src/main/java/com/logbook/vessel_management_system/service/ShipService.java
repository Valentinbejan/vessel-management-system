package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.CreateShipRequest;
import com.logbook.vessel_management_system.dto.ShipDto;
import com.logbook.vessel_management_system.dto.UpdateShipRequest;

import java.util.List;

public interface ShipService {
    List<ShipDto> getAllShips();
    ShipDto getShipDetailsById(Long shipId); // Parameter name changed to shipId
    ShipDto createShip(CreateShipRequest request);
    ShipDto updateShip(Long shipId, UpdateShipRequest request); // Parameter name changed
    void deleteShip(Long shipId); // Parameter name changed
}