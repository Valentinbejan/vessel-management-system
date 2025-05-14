package com.logbook.vessel_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logbook.vessel_management_system.dto.CreateShipRequest;
import com.logbook.vessel_management_system.dto.UpdateShipRequest;
import com.logbook.vessel_management_system.entity.Owner;
import com.logbook.vessel_management_system.entity.Ship;
import com.logbook.vessel_management_system.entity.ShipCategoryDetails;
import com.logbook.vessel_management_system.repository.OwnerRepository;
import com.logbook.vessel_management_system.repository.ShipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
@Rollback
class ShipControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner testOwner1;
    private Owner testOwner2;

    @BeforeEach
    void setUp() {
        // Clean repositories
        shipRepository.deleteAll();
        ownerRepository.deleteAll();
        
        // Create test data fresh for each test
        testOwner1 = ownerRepository.save(new Owner("Test Owner Alpha"));
        testOwner2 = ownerRepository.save(new Owner("Test Owner Beta"));
    }

    @Test
    void testGetAllShips() throws Exception {
        // Create ships using the service layer through API calls
        CreateShipRequest request1 = new CreateShipRequest();
        request1.setShipName("Initial Ship");
        request1.setImoNumber("1111111");
        request1.setShipType("Cruise");
        request1.setShipTonnage(100000);
        request1.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        CreateShipRequest request2 = new CreateShipRequest();
        request2.setShipName("Second Ship");
        request2.setImoNumber("2222222");
        request2.setShipType("Cargo");
        request2.setShipTonnage(70000);
        request2.setOwnerIds(Set.of(testOwner2.getOwnerId()));

        // Create ships via API
        mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Test get all ships
        mockMvc.perform(get("/api/v1/ships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].shipName", containsInAnyOrder("Initial Ship", "Second Ship")));
    }

    @Test
    void testCreateShip_Success() throws Exception {
        CreateShipRequest request = new CreateShipRequest();
        request.setShipName("New Awesome Ship");
        request.setImoNumber("3333333");
        request.setShipType("Tanker");
        request.setShipTonnage(50000);
        request.setOwnerIds(Set.of(testOwner1.getOwnerId(), testOwner2.getOwnerId()));

        mockMvc.perform(post("/api/v1/ships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", isA(Number.class)))
                .andExpect(jsonPath("$.shipName", is("New Awesome Ship")))
                .andExpect(jsonPath("$.imoNumber", is("3333333")))
                .andExpect(jsonPath("$.shipType", is("Tanker")))
                .andExpect(jsonPath("$.shipTonnage", is(50000)))
                .andExpect(jsonPath("$.ownerIds", hasSize(2)))
                .andExpect(jsonPath("$.ownerIds", containsInAnyOrder(
                        testOwner1.getOwnerId().intValue(),
                        testOwner2.getOwnerId().intValue()
                )));

        assertTrue(shipRepository.findByImoNumber("3333333").isPresent());
    }

    @Test
    void testCreateShip_ValidationFailure_MissingName() throws Exception {
        CreateShipRequest request = new CreateShipRequest();
        // shipName is intentionally omitted
        request.setImoNumber("4444444");
        request.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        mockMvc.perform(post("/api/v1/ships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.shipName", is("Ship name cannot be blank")));
    }

    @Test
    void testCreateShip_DuplicateImoNumber() throws Exception {
        // First, create a ship
        CreateShipRequest firstRequest = new CreateShipRequest();
        firstRequest.setShipName("First Ship");
        firstRequest.setImoNumber("1111111");
        firstRequest.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Then try to create another ship with the same IMO number
        CreateShipRequest duplicateRequest = new CreateShipRequest();
        duplicateRequest.setShipName("Another Ship");
        duplicateRequest.setImoNumber("1111111"); // Same IMO number
        duplicateRequest.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        mockMvc.perform(post("/api/v1/ships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void testGetShipDetails_Success() throws Exception {
        // Create a ship first
        CreateShipRequest request = new CreateShipRequest();
        request.setShipName("Test Ship Details");
        request.setImoNumber("1111111");
        request.setShipType("Cruise");
        request.setShipTonnage(100000);
        request.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        String responseJson = mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract ship ID from response
        Long shipId = objectMapper.readTree(responseJson).get("id").asLong();

        // Test get ship details
        mockMvc.perform(get("/api/v1/ships/{shipId}/details", shipId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(shipId.intValue())))
                .andExpect(jsonPath("$.shipName", is("Test Ship Details")))
                .andExpect(jsonPath("$.imoNumber", is("1111111")))
                .andExpect(jsonPath("$.shipType", is("Cruise")))
                .andExpect(jsonPath("$.shipTonnage", is(100000)))
                .andExpect(jsonPath("$.ownerIds", hasSize(1)))
                .andExpect(jsonPath("$.ownerIds", containsInAnyOrder(testOwner1.getOwnerId().intValue())));
    }

    @Test
    void testGetShipDetails_NotFound() throws Exception {
        long nonExistentShipId = 999L;
        mockMvc.perform(get("/api/v1/ships/{shipId}/details", nonExistentShipId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateShip_Success() throws Exception {
        // Create a ship first
        CreateShipRequest createRequest = new CreateShipRequest();
        createRequest.setShipName("Original Ship");
        createRequest.setImoNumber("1111111");
        createRequest.setShipType("Cruise");
        createRequest.setShipTonnage(100000);
        createRequest.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        String responseJson = mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long shipId = objectMapper.readTree(responseJson).get("id").asLong();

        // Update the ship
        UpdateShipRequest updateRequest = new UpdateShipRequest();
        updateRequest.setShipName("Updated Ship Name");
        updateRequest.setShipType("Ro-Ro");
        updateRequest.setShipTonnage(120000);
        updateRequest.setOwnerIds(Set.of(testOwner2.getOwnerId()));

        mockMvc.perform(put("/api/v1/ships/{shipId}", shipId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(shipId.intValue())))
                .andExpect(jsonPath("$.shipName", is("Updated Ship Name")))
                .andExpect(jsonPath("$.shipType", is("Ro-Ro")))
                .andExpect(jsonPath("$.shipTonnage", is(120000)))
                .andExpect(jsonPath("$.ownerIds", hasSize(1)))
                .andExpect(jsonPath("$.ownerIds", containsInAnyOrder(testOwner2.getOwnerId().intValue())));
    }

    @Test
    void testUpdateShip_AddDetailsToShipWithoutThem() throws Exception {
        // Create a ship without details
        CreateShipRequest createRequest = new CreateShipRequest();
        createRequest.setShipName("Plain Ship");
        createRequest.setImoNumber("5555555");
        // Don't set shipType or shipTonnage
        createRequest.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        String responseJson = mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long shipId = objectMapper.readTree(responseJson).get("id").asLong();

        // Update the ship to add details
        UpdateShipRequest updateRequest = new UpdateShipRequest();
        updateRequest.setShipName("Plain Ship With Details");
        updateRequest.setShipType("Bulk Carrier");
        updateRequest.setShipTonnage(75000);
        updateRequest.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        mockMvc.perform(put("/api/v1/ships/{shipId}", shipId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipName", is("Plain Ship With Details")))
                .andExpect(jsonPath("$.shipType", is("Bulk Carrier")))
                .andExpect(jsonPath("$.shipTonnage", is(75000)));
    }

    @Test
    void testDeleteShip_Success() throws Exception {
        // Create a ship first
        CreateShipRequest createRequest = new CreateShipRequest();
        createRequest.setShipName("Ship to Delete");
        createRequest.setImoNumber("1111111");
        createRequest.setOwnerIds(Set.of(testOwner1.getOwnerId()));

        String responseJson = mockMvc.perform(post("/api/v1/ships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long shipId = objectMapper.readTree(responseJson).get("id").asLong();

        // Verify ship exists
        assertTrue(shipRepository.existsById(shipId));

        // Delete the ship
        mockMvc.perform(delete("/api/v1/ships/{shipId}", shipId))
                .andExpect(status().isNoContent());

        // Verify ship no longer exists
        assertFalse(shipRepository.existsById(shipId));
    }

    @Test
    void testDeleteShip_NotFound() throws Exception {
        long nonExistentShipId = 888L;
        mockMvc.perform(delete("/api/v1/ships/{shipId}", nonExistentShipId))
                .andExpect(status().isNotFound());
    }
}