package ru.mfa.rbpo2026.dto;

import lombok.Data;

import ru.mfa.rbpo2026.entity.RouteStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RouteDto {
    private UUID id;
    private LocalDate date;
    private BigDecimal maxMass;
    private RouteStatus status;
}
