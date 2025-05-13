package com.logbook.vessel_management_system.repository;

import com.logbook.vessel_management_system.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByOwnerName(String ownerName); // Example custom query
}