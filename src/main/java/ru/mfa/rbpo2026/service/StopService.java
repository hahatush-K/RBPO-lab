package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.entity.Stop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StopService {

    private final ShipmentAssignmentService assignmentService;

    Map<UUID, Stop> stops = new HashMap<>();

    public List<Stop> getStops() {
        return new ArrayList<>(stops.values());
    }

    public Stop getStop(UUID id) {
        return stops.get(id);
    }

    public Stop addStop(Stop stop) {
        stop.setId(UUID.randomUUID());
        stops.put(stop.getId(), stop);
        return stop;
    }

    public Stop updateStop(UUID id, Stop patch) {
        var stop = stops.get(id);
        if (stop == null) {
            return null;
        }
        if (patch.getRouteId() != null) stop.setRouteId(patch.getRouteId());
        if (patch.getAddress() != null) stop.setAddress(patch.getAddress());
        if (patch.getOrderNumber() != null) stop.setOrderNumber(patch.getOrderNumber());
        return stop;
    }

    public void deleteStop(UUID id) {
        assignmentService.deleteByStopId(id);
        stops.remove(id);
    }

    public void deleteByRouteId(UUID routeId) {
        List<UUID> ids = new ArrayList<>();
        for (Stop stop : stops.values()) {
            if (routeId.equals(stop.getRouteId())) {
                ids.add(stop.getId());
            }
        }
        for (UUID id : ids) {
            deleteStop(id);
        }
    }

}