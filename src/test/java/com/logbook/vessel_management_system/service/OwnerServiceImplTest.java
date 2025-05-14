package com.logbook.vessel_management_system.service;

import com.logbook.vessel_management_system.dto.OwnerDto;
import com.logbook.vessel_management_system.entity.Owner;
import com.logbook.vessel_management_system.entity.Ship;
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
class OwnerServiceImplTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private ShipRepository shipRepository;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private Owner testOwner1;
    private Owner testOwner2;
    private Ship testShip1;
    private Ship testShip2;

    @BeforeEach
    void setUp() {
        // Create test entities
        testOwner1 = new Owner("Test Owner 1");
        testOwner1.setOwnerId(1L);

        testOwner2 = new Owner("Test Owner 2");
        testOwner2.setOwnerId(2L);

        testShip1 = new Ship("Test Ship 1", "1111111");
        testShip1.setId(1L);

        testShip2 = new Ship("Test Ship 2", "2222222");
        testShip2.setId(2L);

        // Set up relationships
        testOwner1.addShip(testShip1);
        testOwner1.addShip(testShip2);
        testShip1.addOwner(testOwner1);
        testShip2.addOwner(testOwner1);
    }

    @Test
    void getAllOwners_ShouldReturnAllOwners() {
        // Given
        List<Owner> owners = Arrays.asList(testOwner1, testOwner2);
        when(ownerRepository.findAll()).thenReturn(owners);

        // When
        List<OwnerDto> result = ownerService.getAllOwners();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOwnerName()).isEqualTo("Test Owner 1");
        assertThat(result.get(0).getShipIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.get(1).getOwnerName()).isEqualTo("Test Owner 2");
        assertThat(result.get(1).getShipIds()).isEmpty();
        
        verify(ownerRepository).findAll();
    }

    @Test
    void createOwner_WithValidData_ShouldCreateOwner() {
        // Given
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setOwnerName("New Owner");
        ownerDto.setShipIds(new HashSet<>());

        when(ownerRepository.findByOwnerName("New Owner")).thenReturn(Optional.empty());
        
        Owner savedOwner = new Owner("New Owner");
        savedOwner.setOwnerId(3L);
        when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

        // When
        OwnerDto result = ownerService.createOwner(ownerDto);

        // Then
        assertThat(result.getOwnerId()).isEqualTo(3L);
        assertThat(result.getOwnerName()).isEqualTo("New Owner");
        assertThat(result.getShipIds()).isEmpty();
        
        verify(ownerRepository).findByOwnerName("New Owner");
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void createOwner_WithDuplicateName_ShouldThrowException() {
        // Given
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setOwnerName("Test Owner 1"); // Already exists

        when(ownerRepository.findByOwnerName("Test Owner 1")).thenReturn(Optional.of(testOwner1));

        // When & Then
        assertThatThrownBy(() -> ownerService.createOwner(ownerDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        
        verify(ownerRepository).findByOwnerName("Test Owner 1");
        verify(ownerRepository, never()).save(any());
    }

    @Test
    void deleteOwner_WithNoShips_ShouldDeleteOwner() {
        // Given
        Owner ownerWithoutShips = new Owner("Owner Without Ships");
        ownerWithoutShips.setOwnerId(3L);
        ownerWithoutShips.setShips(new HashSet<>()); // No ships

        when(ownerRepository.findById(3L)).thenReturn(Optional.of(ownerWithoutShips));

        // When
        ownerService.deleteOwner(3L);

        // Then
        verify(ownerRepository).findById(3L);
        verify(shipRepository, never()).save(any()); // No ships to update
        verify(ownerRepository).delete(ownerWithoutShips);
    }

    @Test
    void deleteOwner_WithShips_ShouldRemoveOwnershipAndDeleteOwner() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner1));

        // When
        ownerService.deleteOwner(1L);

        // Then
        verify(ownerRepository).findById(1L);
        
        // Should save each ship that had this owner removed
        verify(shipRepository).save(testShip1);
        verify(shipRepository).save(testShip2);
        
        verify(ownerRepository).delete(testOwner1);
        
        // Verify that the ships no longer have this owner
        assertThat(testShip1.getOwners()).doesNotContain(testOwner1);
        assertThat(testShip2.getOwners()).doesNotContain(testOwner1);
    }

    @Test
    void deleteOwner_WhenOwnerNotFound_ShouldThrowException() {
        // Given
        when(ownerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ownerService.deleteOwner(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner not found");
        
        verify(ownerRepository).findById(999L);
        verify(shipRepository, never()).save(any());
        verify(ownerRepository, never()).delete(any());
    }

    @Test
    void createOwner_WithNullShipIds_ShouldCreateOwnerWithEmptyShipSet() {
        // Given
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setOwnerName("Owner With Null Ships");
        ownerDto.setShipIds(null); // Null ship IDs

        when(ownerRepository.findByOwnerName("Owner With Null Ships")).thenReturn(Optional.empty());
        
        Owner savedOwner = new Owner("Owner With Null Ships");
        savedOwner.setOwnerId(4L);
        when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

        // When
        OwnerDto result = ownerService.createOwner(ownerDto);

        // Then
        assertThat(result.getOwnerId()).isEqualTo(4L);
        assertThat(result.getOwnerName()).isEqualTo("Owner With Null Ships");
        assertThat(result.getShipIds()).isEmpty(); // Should be empty set, not null
        
        verify(ownerRepository).findByOwnerName("Owner With Null Ships");
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void getAllOwners_WhenNoOwners_ShouldReturnEmptyList() {
        // Given
        when(ownerRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<OwnerDto> result = ownerService.getAllOwners();

        // Then
        assertThat(result).isEmpty();
        verify(ownerRepository).findAll();
    }

    @Test
    void deleteOwner_WithComplexOwnershipStructure_ShouldHandleCorrectly() {
        // Given
        Owner multiOwner = new Owner("Multi Owner");
        multiOwner.setOwnerId(5L);
        
        Ship sharedShip = new Ship("Shared Ship", "3333333");
        sharedShip.setId(3L);
        
        // Set up complex ownership - ship has multiple owners
        multiOwner.addShip(sharedShip);
        testOwner2.addShip(sharedShip);
        sharedShip.addOwner(multiOwner);
        sharedShip.addOwner(testOwner2);

        when(ownerRepository.findById(5L)).thenReturn(Optional.of(multiOwner));

        // When
        ownerService.deleteOwner(5L);

        // Then
        verify(ownerRepository).findById(5L);
        verify(shipRepository).save(sharedShip);
        verify(ownerRepository).delete(multiOwner);
        
        // Verify that the ship still has testOwner2 but not multiOwner
        assertThat(sharedShip.getOwners()).contains(testOwner2);
        assertThat(sharedShip.getOwners()).doesNotContain(multiOwner);
    }
}