package ru.mfa.rbpo2026.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class ShipmentAssignment {

    private UUID id;
    private UUID shipmentId;
    private UUID stopId;
    private String status;

}
