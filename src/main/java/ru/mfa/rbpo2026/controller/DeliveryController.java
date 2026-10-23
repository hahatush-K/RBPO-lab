package ru.mfa.rbpo2026.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.rbpo2026.dto.AssignShipmentRequest;
import ru.mfa.rbpo2026.dto.RouteDto;
import ru.mfa.rbpo2026.dto.ShipmentAssignmentDto;
import ru.mfa.rbpo2026.service.DeliveryService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService service;

    // POST /api/shipments/{id}/assign  {"stopId": "..."}
    @PostMapping("/shipments/{id}/assign")
    public ResponseEntity<ShipmentAssignmentDto> assign(@PathVariable UUID id, @RequestBody AssignShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.assignShipment(id, request.getStopId()));
    }

    @PostMapping("/shipment-assignments/{id}/unassign")
    public ResponseEntity<ShipmentAssignmentDto> unassign(@PathVariable UUID id) {
        return ResponseEntity.ok(service.unassign(id));
    }

    @PostMapping("/shipment-assignments/{id}/deliver")
    public ResponseEntity<ShipmentAssignmentDto> deliver(@PathVariable UUID id) {
        return ResponseEntity.ok(service.deliver(id));
    }

    @PostMapping("/routes/{id}/start")
    public ResponseEntity<RouteDto> start(@PathVariable UUID id) {
        return ResponseEntity.ok(service.startRoute(id));
    }

    @PostMapping("/routes/{id}/cancel")
    public ResponseEntity<RouteDto> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelRoute(id));
    }

}
