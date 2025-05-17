package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.OwnerDto;
import com.logbook.vessel_management_system.entity.Owner;
import com.logbook.vessel_management_system.entity.Ship;
import com.logbook.vessel_management_system.exception.ResourceNotFoundException;
import com.logbook.vessel_management_system.repository.OwnerRepository;
import com.logbook.vessel_management_system.repository.ShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet; // Added import
import java.util.List;
import java.util.Set;     // Added import
import java.util.stream.Collectors;

/**
 * Implementation of the OwnerService interface.
 * 
 * Dependency Injection Pattern: Uses constructor injection via @RequiredArgsConstructor
 * to inject required dependencies, promoting loose coupling.
 * 
 * Facade Pattern: Provides a simplified interface for owner management operations,
 * hiding the complexity of repositories and entity relationships.
 */

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final ShipRepository shipRepository; // Now used

    /**
     * Proxy Pattern: The @Transactional annotation triggers Spring to create a proxy
     * around this method that handles transaction boundaries.
     * 
     * Template Method Pattern: Concrete implementation of an abstract operation
     * defined in the OwnerService interface.
     */

    @Override
    @Transactional(readOnly = true)
    public List<OwnerDto> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(this::mapToOwnerDto)
                .collect(Collectors.toList());
    }

    /**
     * Command Pattern: Executes a specific operation (creating an owner)
     * that changes the state of the system.
     */

    @Override
    @Transactional
    public OwnerDto createOwner(OwnerDto ownerDto) {
        if (ownerRepository.findByOwnerName(ownerDto.getOwnerName()).isPresent()){
            throw new IllegalArgumentException("Owner with name " + ownerDto.getOwnerName() + " already exists.");
        }
        Owner owner = new Owner(ownerDto.getOwnerName());
        Owner savedOwner = ownerRepository.save(owner);
        return mapToOwnerDto(savedOwner);
    }

    /**
     * Facade Pattern: This method hides complex operations involving both
     * Owner and Ship entities, providing a simple deleteOwner operation
     * that handles all the internal complexity.
     */

    @Override
    @Transactional
    public void deleteOwner(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        // Create a copy of the set to iterate over, to avoid ConcurrentModificationException
        // if the underlying collection is modified by ship.removeOwner indirectly.
        Set<Ship> shipsOwned = new HashSet<>(owner.getShips());
        for (Ship ship : shipsOwned) {
            // Ensure Ship's removeOwner method correctly updates both sides of the relationship
            // or at least prepares the Ship entity for saving.
            ship.removeOwner(owner);
            shipRepository.save(ship); // Persist changes to the Ship entity (updates join table)
        }
        // At this point, Hibernate's session should be aware that the links from Ship entities
        // to this Owner are severed. The ON DELETE CASCADE at the DB level for Owner_Id_FK
        // in Ship_Ownership_Link_Table will handle the physical deletion of link records
        // when the owner record itself is deleted.

        ownerRepository.delete(owner);
    }

    /**
     * Adapter Pattern: Transforms/adapts the Owner entity to an OwnerDto
     * for the presentation layer, keeping entity details internal.
     */

    private OwnerDto mapToOwnerDto(Owner owner) {
        OwnerDto dto = new OwnerDto();
        dto.setOwnerId(owner.getOwnerId());
        dto.setOwnerName(owner.getOwnerName());
        if (owner.getShips() != null) {
            dto.setShipIds(owner.getShips().stream()
                              .map(Ship::getId)
                              .collect(Collectors.toSet()));
        } else {
            dto.setShipIds(new HashSet<>());
        }
        return dto;
    }
}