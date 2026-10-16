package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.entity.Shipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentAssignmentService assignmentService;

    Map<UUID, Shipment> shipments = new HashMap<>();

    public List<Shipment> getShipments() {
        return new ArrayList<>(shipments.values());
    }

    public Shipment getShipment(UUID id) {
        return shipments.get(id);
    }

    public Shipment addShipment(Shipment shipment) {
        shipment.setId(UUID.randomUUID());
        shipments.put(shipment.getId(), shipment);
        return shipment;
    }

    public Shipment updateShipment(UUID id, Shipment patch) {
        var shipment = shipments.get(id);
        if (shipment == null) {
            return null;
        }
        if (patch.getSenderId() != null) shipment.setSenderId(patch.getSenderId());
        if (patch.getAddress() != null) shipment.setAddress(patch.getAddress());
        if (patch.getMass() != null) shipment.setMass(patch.getMass());
        if (patch.getStatus() != null) shipment.setStatus(patch.getStatus());
        return shipment;
    }

    public void deleteShipment(UUID id) {
        assignmentService.deleteByShipmentId(id);
        shipments.remove(id);
    }

    public void deleteBySenderId(UUID senderId) {
        List<UUID> ids = new ArrayList<>();
        for (Shipment shipment : shipments.values()) {
            if (senderId.equals(shipment.getSenderId())) {
                ids.add(shipment.getId());
            }
        }
        for (UUID id : ids) {
            deleteShipment(id);
        }
    }

}