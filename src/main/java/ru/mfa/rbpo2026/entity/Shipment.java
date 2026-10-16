package ru.mfa.rbpo2026.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class Shipment {

    private UUID id;
    private UUID senderId;
    private String address;
    private Double mass;
    private String status;

}
