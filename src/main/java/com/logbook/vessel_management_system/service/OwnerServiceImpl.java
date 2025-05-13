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

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final ShipRepository shipRepository; // Now used

    @Override
    @Transactional(readOnly = true)
    public List<OwnerDto> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(this::mapToOwnerDto)
                .collect(Collectors.toList());
    }

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