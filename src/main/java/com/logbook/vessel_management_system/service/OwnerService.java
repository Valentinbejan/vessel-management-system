package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.OwnerDto;
import java.util.List;

public interface OwnerService {
    List<OwnerDto> getAllOwners();
    OwnerDto createOwner(OwnerDto ownerDto); // Example: if you want to create owners
    void deleteOwner(Long ownerId);
}