package ru.mfa.rbpo2026.dto;

import lombok.Data;

import ru.mfa.rbpo2026.entity.AssignmentStatus;

import java.util.UUID;

@Data
public class ShipmentAssignmentDto {
    private UUID id;
    private UUID shipmentId;
    private UUID stopId;
    private AssignmentStatus status;
}
