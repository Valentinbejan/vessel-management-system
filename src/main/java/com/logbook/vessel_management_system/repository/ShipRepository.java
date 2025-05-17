package com.logbook.vessel_management_system.repository;

import com.logbook.vessel_management_system.entity.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository Pattern: Abstracts the data access layer completely, providing a collection-like interface
 * for domain objects. This pattern isolates the domain model from the details of the database access code.
 * Spring Data JPA implements the Repository pattern by generating the implementation at runtime.
 */

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> { // Ship ID is Long

     /**
      * Repository Pattern: Custom query method that demonstrates how the repository pattern
      * can encapsulate complex queries while still providing a clean abstract interface.
      * The JPQL query with JOIN FETCH is hidden from the service layer that uses this method.
      */

     @Query("SELECT s FROM Ship s LEFT JOIN FETCH s.details LEFT JOIN FETCH s.owners WHERE s.id = :shipId")
     Optional<Ship> findByIdWithDetailsAndOwners(@Param("shipId") Long shipId);

     /**
      * Repository Pattern: Method name conventions are used to automatically generate query
      * implementations, abstracting the data access details.
      */

     Optional<Ship> findByImoNumber(String imoNumber);
}