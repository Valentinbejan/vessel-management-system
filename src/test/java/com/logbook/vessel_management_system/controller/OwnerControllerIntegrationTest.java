package com.logbook.vessel_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logbook.vessel_management_system.dto.OwnerDto;
import com.logbook.vessel_management_system.entity.Owner;
import com.logbook.vessel_management_system.entity.Ship;
import com.logbook.vessel_management_system.repository.OwnerRepository;
import com.logbook.vessel_management_system.repository.ShipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ShipRepository shipRepository;

    private Owner owner1;
    private Owner owner2;

    @BeforeEach
    void setUp() {
        // Create test data fresh for each test
        owner1 = ownerRepository.save(new Owner("Royal Caribbean"));
        owner2 = ownerRepository.save(new Owner("Carnival Cruises"));
    }

    @Test
    void testGetAllOwners() throws Exception {
        mockMvc.perform(get("/api/v1/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].ownerName", containsInAnyOrder("Royal Caribbean", "Carnival Cruises")));
    }

    @Test
    void testCreateOwner_Success() throws Exception {
        OwnerDto newOwnerDto = new OwnerDto();
        newOwnerDto.setOwnerName("MSC Cruises");
        newOwnerDto.setShipIds(new HashSet<>());

        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOwnerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId", isA(Number.class)))
                .andExpect(jsonPath("$.ownerName", is("MSC Cruises")));

        assertTrue(ownerRepository.findByOwnerName("MSC Cruises").isPresent());
    }

    @Test
    void testCreateOwner_DuplicateName() throws Exception {
        OwnerDto duplicateOwnerDto = new OwnerDto();
        duplicateOwnerDto.setOwnerName(owner1.getOwnerName());
        duplicateOwnerDto.setShipIds(new HashSet<>());

        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateOwnerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void testDeleteOwner_Success_NoShips() throws Exception {
        Long ownerIdToDelete = owner2.getOwnerId();

        mockMvc.perform(delete("/api/v1/owners/{ownerId}", ownerIdToDelete))
                .andExpect(status().isNoContent());

        assertFalse(ownerRepository.existsById(ownerIdToDelete));
    }

    @Test
    void testDeleteOwner_Success_WithShips() throws Exception {
        // Create a ship and associate it with owner1
        Ship ship = new Ship("Wonder of the Seas", "9837780");
        ship.addOwner(owner1);  // This also adds the ship to owner's collection
        shipRepository.save(ship);

        // Verify owner has ships before deletion
        Owner ownerBeforeDelete = ownerRepository.findById(owner1.getOwnerId()).orElseThrow();
        assertFalse(ownerBeforeDelete.getShips().isEmpty(), "Owner should have ships before deletion");

        Long ownerIdToDelete = owner1.getOwnerId();

        mockMvc.perform(delete("/api/v1/owners/{ownerId}", ownerIdToDelete))
                .andExpect(status().isNoContent());

        assertFalse(ownerRepository.existsById(ownerIdToDelete), "Owner should be deleted");

        Ship shipAfterOwnerDelete = shipRepository.findById(ship.getId()).orElseThrow();
        assertTrue(shipAfterOwnerDelete.getOwners().isEmpty(), "Ship should have no owners after its owner was deleted");
    }

    @Test
    void testDeleteOwner_NotFound() throws Exception {
        long nonExistentOwnerId = 9999L;
        mockMvc.perform(delete("/api/v1/owners/{ownerId}", nonExistentOwnerId))
                .andExpect(status().isNotFound());
    }
}