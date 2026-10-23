package ru.mfa.rbpo2026.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.rbpo2026.dto.ShipmentAssignmentDto;
import ru.mfa.rbpo2026.service.ShipmentAssignmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipment-assignments")
@RequiredArgsConstructor
public class ShipmentAssignmentController {
    private final ShipmentAssignmentService service;

    @GetMapping
    public ResponseEntity<List<ShipmentAssignmentDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentAssignmentDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<ShipmentAssignmentDto> create(@RequestBody ShipmentAssignmentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ShipmentAssignmentDto> update(@PathVariable UUID id, @RequestBody ShipmentAssignmentDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
