package ru.mfa.rbpo2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.rbpo2026.entity.AssignmentStatus;
import ru.mfa.rbpo2026.entity.ShipmentAssignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentAssignmentRepository extends JpaRepository<ShipmentAssignment, UUID> {

    List<ShipmentAssignment> findByStopRouteIdAndStatus(UUID routeId, AssignmentStatus status);

    List<ShipmentAssignment> findByStopRouteIdAndStatusNot(UUID routeId, AssignmentStatus status);

    boolean existsByStopRouteIdAndStatus(UUID routeId, AssignmentStatus status);

    @Query("SELECT a.stop.route.id FROM ShipmentAssignment a WHERE a.id = :id")
    Optional<UUID> findRouteIdById(@Param("id") UUID id);

}
