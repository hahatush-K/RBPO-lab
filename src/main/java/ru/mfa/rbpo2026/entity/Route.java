package ru.mfa.rbpo2026.entity;

import lombok.Data;

import java.util.UUID;

import java.time.LocalDate;

@Data
public class Route {

    private UUID id;
    private LocalDate date;
    private Double maxMass;
    private String status;

}
