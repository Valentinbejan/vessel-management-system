package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.OwnerDto;
import java.util.List;

/**
 * Facade Pattern: Provides a simplified interface for owner management operations,
 * hiding the complexity of repositories and entity relationships.
 */

public interface OwnerService {
    List<OwnerDto> getAllOwners();
    OwnerDto createOwner(OwnerDto ownerDto); // Example: if you want to create owners
    void deleteOwner(Long ownerId);
}