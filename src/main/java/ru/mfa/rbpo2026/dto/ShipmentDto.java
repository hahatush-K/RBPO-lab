package ru.mfa.rbpo2026.dto;

import lombok.Data;

import ru.mfa.rbpo2026.entity.ShipmentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ShipmentDto {
    private UUID id;
    private UUID senderId;
    private String address;
    private BigDecimal mass;
    private ShipmentStatus status;
}
