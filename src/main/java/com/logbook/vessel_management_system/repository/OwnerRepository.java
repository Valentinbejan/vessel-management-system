package com.logbook.vessel_management_system.repository;

import com.logbook.vessel_management_system.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Pattern: Provides a mechanism to encapsulate storage, retrieval, and search behaviors
 * for Owner entities. The pattern removes direct database access from the business logic.
 */

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    /**
     * Repository Pattern: Method name conventions define the query implementation that's
     * generated at runtime, further abstracting database access code.
     */

    Optional<Owner> findByOwnerName(String ownerName); // Example custom query
}