package com.logbook.vessel_management_system.repository;

import com.logbook.vessel_management_system.entity.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> { // Ship ID is Long

     @Query("SELECT s FROM Ship s LEFT JOIN FETCH s.details LEFT JOIN FETCH s.owners WHERE s.id = :shipId")
     Optional<Ship> findByIdWithDetailsAndOwners(@Param("shipId") Long shipId);

     Optional<Ship> findByImoNumber(String imoNumber);
}