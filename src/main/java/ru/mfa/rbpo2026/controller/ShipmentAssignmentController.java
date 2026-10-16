package ru.mfa.rbpo2026.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.rbpo2026.entity.ShipmentAssignment;
import ru.mfa.rbpo2026.service.ShipmentAssignmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipment-assignments")
public class ShipmentAssignmentController {

    private final ShipmentAssignmentService assignmentService;

    @GetMapping
    public ResponseEntity<List<ShipmentAssignment>> getAssignments() {
        return ResponseEntity.ok(assignmentService.getAssignments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentAssignment> getAssignment(@PathVariable UUID id) {
        var assignment = assignmentService.getAssignment(id);
        if (assignment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(assignment);
    }

    @PostMapping
    public ResponseEntity<ShipmentAssignment> addAssignment(@RequestBody ShipmentAssignment assignment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.addAssignment(assignment));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ShipmentAssignment> updateAssignment(@PathVariable UUID id,
                                                               @RequestBody ShipmentAssignment assignment) {
        var updated = assignmentService.updateAssignment(id, assignment);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable UUID id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

}