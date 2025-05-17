// src/main/java/com/logbook/vessel_management_system/service/ShipServiceImpl.java
package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.CreateShipRequest;
import com.logbook.vessel_management_system.dto.ShipDto;
import com.logbook.vessel_management_system.dto.UpdateShipRequest;
import com.logbook.vessel_management_system.entity.Owner;
import com.logbook.vessel_management_system.entity.Ship;
import com.logbook.vessel_management_system.entity.ShipCategoryDetails;
import com.logbook.vessel_management_system.exception.ResourceNotFoundException;
import com.logbook.vessel_management_system.repository.OwnerRepository;
import com.logbook.vessel_management_system.repository.ShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the ShipService interface.
 * 
 * Dependency Injection Pattern: Uses constructor injection through Lombok's
 * @RequiredArgsConstructor, making dependencies explicit and testable.
 * 
 * Facade Pattern: Provides a simplified interface to the complex subsystem of
 * repositories, entities, and their relationships.
 */

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;
    private final OwnerRepository ownerRepository;

    /**
     * Template Method Pattern: Concrete implementation of an abstract operation
     * defined in the ShipService interface.
     * 
     * Transaction Management through Proxy Pattern: The @Transactional annotation
     * triggers Spring to create a proxy that handles transaction boundaries.
     */

    @Override
    @Transactional(readOnly = true)
    public List<ShipDto> getAllShips() {
        return shipRepository.findAll().stream()
                .map(this::mapToShipDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShipDto getShipById(Long shipId) {
        Ship ship = shipRepository.findByIdWithDetailsAndOwners(shipId)
                .orElseThrow(() -> new ResourceNotFoundException("Ship", "id", shipId));
        return mapToShipDto(ship);
    }

    /**
     * Facade Pattern: Hides the complexity of fetching data from repositories
     * and mapping between entities and DTOs.
     * 
     * Proxy Pattern: Transaction management is handled transparently.
     */

    @Override
    @Transactional
    public ShipDto createShip(CreateShipRequest request) {
        if (shipRepository.findByImoNumber(request.getImoNumber()).isPresent()) {
            throw new IllegalArgumentException("Ship with IMO number " + request.getImoNumber() + " already exists.");
        }

        Ship ship = new Ship(request.getShipName(), request.getImoNumber());

        if (request.getShipType() != null || request.getShipTonnage() != null) {
            ShipCategoryDetails details = new ShipCategoryDetails(request.getShipType(), request.getShipTonnage());
            ship.setDetails(details);
        }

        // Find and add owners using helper method
        if (request.getOwnerIds() != null && !request.getOwnerIds().isEmpty()) {
            Set<Owner> owners = findAndValidateOwners(request.getOwnerIds());
            for (Owner owner : owners) {
                ship.addOwner(owner);  // Use helper method to manage bidirectional relationship
            }
        }

        Ship savedShip = shipRepository.save(ship);
        return mapToShipDto(savedShip);
    }

     /**
     * Command Pattern: Method executes a specific operation that changes the state
     * of the system based on the input request.
     */

    @Override
    @Transactional
    public ShipDto updateShip(Long shipId, UpdateShipRequest request) {
        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new ResourceNotFoundException("Ship", "id", shipId));

        ship.setShipName(request.getShipName());

        ShipCategoryDetails details = ship.getDetails();
        if (request.getShipType() != null || request.getShipTonnage() != null) {
            if (details == null) {
                details = new ShipCategoryDetails();
                ship.setDetails(details);
            }
            details.setShipType(request.getShipType());
            details.setShipTonnage(request.getShipTonnage());
        }

        if (request.getOwnerIds() != null) {
            // Remove current owners
            Set<Owner> currentOwners = new HashSet<>(ship.getOwners());
            for (Owner owner : currentOwners) {
                ship.removeOwner(owner);
            }
            
            // Add new owners
            Set<Owner> newOwners = findAndValidateOwners(request.getOwnerIds());
            for (Owner owner : newOwners) {
                ship.addOwner(owner);
            }
        }

        Ship updatedShip = shipRepository.save(ship);
        
        // Fetch again with details for the response DTO
        return mapToShipDto(shipRepository.findByIdWithDetailsAndOwners(updatedShip.getId()).get());
    }

    @Override
    @Transactional
    public void deleteShip(Long shipId) {
        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new ResourceNotFoundException("Ship", "id", shipId));
                
        // Remove all owner relationships before deleting the ship
        Set<Owner> currentOwners = new HashSet<>(ship.getOwners());
        for (Owner owner : currentOwners) {
            ship.removeOwner(owner);
        }
        
        shipRepository.delete(ship);
    }

    private Set<Owner> findAndValidateOwners(Set<Long> ownerIds) {
        if (ownerIds == null || ownerIds.isEmpty()) {
             throw new IllegalArgumentException("Owner IDs cannot be empty for ship association.");
        }
        List<Owner> foundOwners = ownerRepository.findAllById(ownerIds);
        if (foundOwners.size() != ownerIds.size()) {
            Set<Long> foundIds = foundOwners.stream().map(Owner::getOwnerId).collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(ownerIds);
            missingIds.removeAll(foundIds);
            throw new ResourceNotFoundException("Owner(s)", "id(s)", missingIds.toString());
        }
        return new HashSet<>(foundOwners);
    }

    /**
     * Adapter Pattern: Helper method that adapts/transforms the Ship entity into
     * a ShipDto for the presentation layer. This isolates the internal data model
     * from what's exposed through the API.
     */

    private ShipDto mapToShipDto(Ship ship) {
        ShipDto dto = new ShipDto();
        dto.setId(ship.getId());
        dto.setShipName(ship.getShipName());
        dto.setImoNumber(ship.getImoNumber());

        if (ship.getDetails() != null) {
            dto.setShipType(ship.getDetails().getShipType());
            dto.setShipTonnage(ship.getDetails().getShipTonnage());
        }

        if (ship.getOwners() != null) {
            dto.setOwnerIds(ship.getOwners().stream()
                                .map(Owner::getOwnerId)
                                .collect(Collectors.toSet()));
        } else {
            dto.setOwnerIds(new HashSet<>());
        }
        return dto;
    }
}