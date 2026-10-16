package ru.mfa.rbpo2026.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.rbpo2026.entity.Shipment;
import ru.mfa.rbpo2026.service.ShipmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<List<Shipment>> getShipments() {
        return ResponseEntity.ok(shipmentService.getShipments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipment(@PathVariable UUID id) {
        var shipment = shipmentService.getShipment(id);
        if (shipment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(shipment);
    }

    @PostMapping
    public ResponseEntity<Shipment> addShipment(@RequestBody Shipment shipment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.addShipment(shipment));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable UUID id, @RequestBody Shipment shipment) {
        var updated = shipmentService.updateShipment(id, shipment);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable UUID id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }

}