package com.logbook.vessel_management_system.dto;

import lombok.Data;
import java.util.Set;

@Data
public class OwnerDto {
    private Long ownerId;
    private String ownerName;
    private Set<Long> shipIds; // IDs of ships owned by this owner
}