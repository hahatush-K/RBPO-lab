package ru.mfa.rbpo2026.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StopDto {
    private UUID id;
    private UUID routeId;
    private String address;
    private Integer orderNumber;
}
