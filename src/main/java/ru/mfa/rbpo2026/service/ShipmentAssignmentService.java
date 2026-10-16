package ru.mfa.rbpo2026.service;

import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.entity.ShipmentAssignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShipmentAssignmentService {

    Map<UUID, ShipmentAssignment> assignments = new HashMap<>();

    public List<ShipmentAssignment> getAssignments() {
        return new ArrayList<>(assignments.values());
    }

    public ShipmentAssignment getAssignment(UUID id) {
        return assignments.get(id);
    }

    public ShipmentAssignment addAssignment(ShipmentAssignment assignment) {
        assignment.setId(UUID.randomUUID());
        assignments.put(assignment.getId(), assignment);
        return assignment;
    }

    public ShipmentAssignment updateAssignment(UUID id, ShipmentAssignment patch) {
        var assignment = assignments.get(id);
        if (assignment == null) {
            return null;
        }
        if (patch.getShipmentId() != null) assignment.setShipmentId(patch.getShipmentId());
        if (patch.getStopId() != null) assignment.setStopId(patch.getStopId());
        if (patch.getStatus() != null) assignment.setStatus(patch.getStatus());
        return assignment;
    }

    public void deleteAssignment(UUID id) {
        assignments.remove(id);
    }

    public void deleteByShipmentId(UUID shipmentId) {
        assignments.values().removeIf(a -> shipmentId.equals(a.getShipmentId()));
    }

    public void deleteByStopId(UUID stopId) {
        assignments.values().removeIf(a -> stopId.equals(a.getStopId()));
    }

}