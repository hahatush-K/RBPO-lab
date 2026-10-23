package ru.mfa.rbpo2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mfa.rbpo2026.entity.Stop;

import java.util.Optional;
import java.util.UUID;

public interface StopRepository extends JpaRepository<Stop, UUID> {

    @Query("SELECT s.route.id FROM Stop s WHERE s.id = :id")
    Optional<UUID> findRouteIdById(@Param("id") UUID id);

}
