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

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;
    private final OwnerRepository ownerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShipDto> getAllShips() {
        return shipRepository.findAll().stream()
                .map(this::mapToShipDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShipDto getShipDetailsById(Long shipId) {
        Ship ship = shipRepository.findByIdWithDetailsAndOwners(shipId)
                .orElseThrow(() -> new ResourceNotFoundException("Ship", "id", shipId));
        return mapToShipDto(ship);
    }

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

        Set<Owner> owners = findAndValidateOwners(request.getOwnerIds());
        ship.setOwners(owners); // Let JPA manage the join table by setting on the owning side

        Ship savedShip = shipRepository.save(ship);
        return mapToShipDto(savedShip);
    }

    @Override
    @Transactional
    public ShipDto updateShip(Long shipId, UpdateShipRequest request) {
        Ship ship = shipRepository.findById(shipId) // Fetch without full details initially
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
        } else if (details != null) { // If request details are null, but existing details are not
            // Decide behavior: remove details or leave as is?
            // For this example, let's assume null in request means no change or explicit removal is another endpoint.
            // If you want to remove details if they are null in request:
            // ship.setDetails(null); // This would cascade delete due to orphanRemoval=true or CascadeType.ALL
        }


        if (request.getOwnerIds() != null) {
            Set<Owner> newOwners = findAndValidateOwners(request.getOwnerIds());
            ship.setOwners(newOwners); // Replace the entire collection
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
        shipRepository.delete(ship); // Cascade settings handle join table and details
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

    private ShipDto mapToShipDto(Ship ship) {
        ShipDto dto = new ShipDto();
        dto.setId(ship.getId()); // Use ship.getId()
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