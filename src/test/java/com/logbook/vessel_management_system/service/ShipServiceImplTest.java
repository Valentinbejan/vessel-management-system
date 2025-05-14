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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipServiceImplTest {

    @Mock
    private ShipRepository shipRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private ShipServiceImpl shipService;

    private Ship testShip;
    private Owner testOwner1;
    private Owner testOwner2;
    private ShipCategoryDetails testDetails;

    @BeforeEach
    void setUp() {
        // Create test entities
        testOwner1 = new Owner("Test Owner 1");
        testOwner1.setOwnerId(1L);

        testOwner2 = new Owner("Test Owner 2");
        testOwner2.setOwnerId(2L);

        testShip = new Ship("Test Ship", "1234567");
        testShip.setId(1L);

        testDetails = new ShipCategoryDetails("Cruise", 100000);
        testDetails.setShipId(1L);
        testShip.setDetails(testDetails);

        testShip.addOwner(testOwner1);
    }

    @Test
    void getAllShips_ShouldReturnAllShips() {
        // Given
        List<Ship> ships = Arrays.asList(testShip);
        when(shipRepository.findAll()).thenReturn(ships);

        // When
        List<ShipDto> result = shipService.getAllShips();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getShipName()).isEqualTo("Test Ship");
        assertThat(result.get(0).getImoNumber()).isEqualTo("1234567");
        verify(shipRepository).findAll();
    }

    @Test
    void getShipDetailsById_WhenShipExists_ShouldReturnShipDto() {
        // Given
        when(shipRepository.findByIdWithDetailsAndOwners(1L)).thenReturn(Optional.of(testShip));

        // When
        ShipDto result = shipService.getShipDetailsById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getShipName()).isEqualTo("Test Ship");
        assertThat(result.getImoNumber()).isEqualTo("1234567");
        assertThat(result.getShipType()).isEqualTo("Cruise");
        assertThat(result.getShipTonnage()).isEqualTo(100000);
        assertThat(result.getOwnerIds()).containsExactly(1L);
        verify(shipRepository).findByIdWithDetailsAndOwners(1L);
    }

    @Test
    void getShipDetailsById_WhenShipNotFound_ShouldThrowException() {
        // Given
        when(shipRepository.findByIdWithDetailsAndOwners(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shipService.getShipDetailsById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ship not found");
        
        verify(shipRepository).findByIdWithDetailsAndOwners(999L);
    }

    @Test
    void createShip_WithValidData_ShouldCreateShip() {
        // Given
        CreateShipRequest request = new CreateShipRequest();
        request.setShipName("New Ship");
        request.setImoNumber("7654321");
        request.setShipType("Cargo");
        request.setShipTonnage(50000);
        request.setOwnerIds(Set.of(1L, 2L));

        when(shipRepository.findByImoNumber("7654321")).thenReturn(Optional.empty());
        when(ownerRepository.findAllById(Set.of(1L, 2L))).thenReturn(Arrays.asList(testOwner1, testOwner2));
        
        Ship savedShip = new Ship("New Ship", "7654321");
        savedShip.setId(2L);
        savedShip.setDetails(new ShipCategoryDetails("Cargo", 50000));
        savedShip.addOwner(testOwner1);
        savedShip.addOwner(testOwner2);
        
        when(shipRepository.save(any(Ship.class))).thenReturn(savedShip);

        // When
        ShipDto result = shipService.createShip(request);

        // Then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getShipName()).isEqualTo("New Ship");
        assertThat(result.getImoNumber()).isEqualTo("7654321");
        assertThat(result.getShipType()).isEqualTo("Cargo");
        assertThat(result.getShipTonnage()).isEqualTo(50000);
        assertThat(result.getOwnerIds()).containsExactlyInAnyOrder(1L, 2L);
        
        verify(shipRepository).findByImoNumber("7654321");
        verify(ownerRepository).findAllById(Set.of(1L, 2L));
        verify(shipRepository).save(any(Ship.class));
    }

    @Test
    void createShip_WithDuplicateImo_ShouldThrowException() {
        // Given
        CreateShipRequest request = new CreateShipRequest();
        request.setShipName("New Ship");
        request.setImoNumber("1234567"); // Same as existing ship
        request.setOwnerIds(Set.of(1L));

        when(shipRepository.findByImoNumber("1234567")).thenReturn(Optional.of(testShip));

        // When & Then
        assertThatThrownBy(() -> shipService.createShip(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        
        verify(shipRepository).findByImoNumber("1234567");
        verify(shipRepository, never()).save(any());
    }

    @Test
    void createShip_WithInvalidOwners_ShouldThrowException() {
        // Given
        CreateShipRequest request = new CreateShipRequest();
        request.setShipName("New Ship");
        request.setImoNumber("7654321");
        request.setOwnerIds(Set.of(1L, 999L)); // 999L doesn't exist

        when(shipRepository.findByImoNumber("7654321")).thenReturn(Optional.empty());
        when(ownerRepository.findAllById(Set.of(1L, 999L))).thenReturn(Arrays.asList(testOwner1)); // Only returns one

        // When & Then
        assertThatThrownBy(() -> shipService.createShip(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner(s) not found");
        
        verify(shipRepository).findByImoNumber("7654321");
        verify(ownerRepository).findAllById(Set.of(1L, 999L));
        verify(shipRepository, never()).save(any());
    }

    @Test
void createShip_WithEmptyOwners_ShouldCreateShipWithoutOwners() {
    // Given
    CreateShipRequest request = new CreateShipRequest();
    request.setShipName("Ship Without Owners");
    request.setImoNumber("7654321");
    request.setOwnerIds(Set.of());

    when(shipRepository.findByImoNumber("7654321")).thenReturn(Optional.empty());
    
    Ship savedShip = new Ship("Ship Without Owners", "7654321");
    savedShip.setId(2L);
    when(shipRepository.save(any(Ship.class))).thenReturn(savedShip);

    // When
    ShipDto result = shipService.createShip(request);

    // Then
    assertThat(result.getOwnerIds()).isEmpty();
    verify(shipRepository).save(any(Ship.class));
}

    @Test
    void updateShip_WithValidData_ShouldUpdateShip() {
        // Given
        UpdateShipRequest request = new UpdateShipRequest();
        request.setShipName("Updated Ship");
        request.setShipType("Tanker");
        request.setShipTonnage(75000);
        request.setOwnerIds(Set.of(2L));

        when(shipRepository.findById(1L)).thenReturn(Optional.of(testShip));
        when(ownerRepository.findAllById(Set.of(2L))).thenReturn(Arrays.asList(testOwner2));
        
        // Create updated ship for return
        Ship updatedShip = new Ship("Updated Ship", "1234567");
        updatedShip.setId(1L);
        ShipCategoryDetails updatedDetails = new ShipCategoryDetails("Tanker", 75000);
        updatedDetails.setShipId(1L);
        updatedShip.setDetails(updatedDetails);
        updatedShip.addOwner(testOwner2);
        
        when(shipRepository.save(any(Ship.class))).thenReturn(updatedShip);
        when(shipRepository.findByIdWithDetailsAndOwners(1L)).thenReturn(Optional.of(updatedShip));

        // When
        ShipDto result = shipService.updateShip(1L, request);

        // Then
        assertThat(result.getShipName()).isEqualTo("Updated Ship");
        assertThat(result.getShipType()).isEqualTo("Tanker");
        assertThat(result.getShipTonnage()).isEqualTo(75000);
        assertThat(result.getOwnerIds()).containsExactly(2L);
        
        verify(shipRepository).findById(1L);
        verify(ownerRepository).findAllById(Set.of(2L));
        verify(shipRepository).save(any(Ship.class));
        verify(shipRepository).findByIdWithDetailsAndOwners(1L);
    }

    @Test
    void updateShip_WhenShipNotFound_ShouldThrowException() {
        // Given
        UpdateShipRequest request = new UpdateShipRequest();
        request.setShipName("Updated Ship");

        when(shipRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shipService.updateShip(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ship not found");
        
        verify(shipRepository).findById(999L);
        verify(shipRepository, never()).save(any());
    }

    @Test
    void updateShip_AddingDetailsToShipWithoutThem_ShouldWork() {
        // Given
        Ship shipWithoutDetails = new Ship("Plain Ship", "1111111");
        shipWithoutDetails.setId(2L);
        shipWithoutDetails.addOwner(testOwner1);

        UpdateShipRequest request = new UpdateShipRequest();
        request.setShipName("Ship With New Details");
        request.setShipType("Ferry");
        request.setShipTonnage(30000);
        request.setOwnerIds(Set.of(1L));

        when(shipRepository.findById(2L)).thenReturn(Optional.of(shipWithoutDetails));
        when(ownerRepository.findAllById(Set.of(1L))).thenReturn(Arrays.asList(testOwner1));
        
        Ship updatedShip = new Ship("Ship With New Details", "1111111");
        updatedShip.setId(2L);
        updatedShip.setDetails(new ShipCategoryDetails("Ferry", 30000));
        updatedShip.addOwner(testOwner1);
        
        when(shipRepository.save(any(Ship.class))).thenReturn(updatedShip);
        when(shipRepository.findByIdWithDetailsAndOwners(2L)).thenReturn(Optional.of(updatedShip));

        // When
        ShipDto result = shipService.updateShip(2L, request);

        // Then
        assertThat(result.getShipName()).isEqualTo("Ship With New Details");
        assertThat(result.getShipType()).isEqualTo("Ferry");
        assertThat(result.getShipTonnage()).isEqualTo(30000);
    }

    @Test
    void deleteShip_WhenShipExists_ShouldDeleteShip() {
        // Given
        when(shipRepository.findById(1L)).thenReturn(Optional.of(testShip));

        // When
        shipService.deleteShip(1L);

        // Then
        verify(shipRepository).findById(1L);
        verify(shipRepository).delete(testShip);
    }

    @Test
    void deleteShip_WhenShipNotFound_ShouldThrowException() {
        // Given
        when(shipRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> shipService.deleteShip(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ship not found");
        
        verify(shipRepository).findById(999L);
        verify(shipRepository, never()).delete(any());
    }

    @Test
    void createShip_WithoutDetails_ShouldCreateShipSuccessfully() {
        // Given
        CreateShipRequest request = new CreateShipRequest();
        request.setShipName("Simple Ship");
        request.setImoNumber("9999999");
        request.setOwnerIds(Set.of(1L));
        // No shipType or shipTonnage set

        when(shipRepository.findByImoNumber("9999999")).thenReturn(Optional.empty());
        when(ownerRepository.findAllById(Set.of(1L))).thenReturn(Arrays.asList(testOwner1));
        
        Ship savedShip = new Ship("Simple Ship", "9999999");
        savedShip.setId(3L);
        savedShip.addOwner(testOwner1);
        // No details set
        
        when(shipRepository.save(any(Ship.class))).thenReturn(savedShip);

        // When
        ShipDto result = shipService.createShip(request);

        // Then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getShipName()).isEqualTo("Simple Ship");
        assertThat(result.getImoNumber()).isEqualTo("9999999");
        assertThat(result.getShipType()).isNull();
        assertThat(result.getShipTonnage()).isNull();
        assertThat(result.getOwnerIds()).containsExactly(1L);
    }
}