package ru.mfa.rbpo2026.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class Stop {

    private UUID id;
    private UUID routeId;
    private String address;
    private Integer orderNumber;

}
