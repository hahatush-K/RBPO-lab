package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.entity.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final StopService stopService;

    Map<UUID, Route> routes = new HashMap<>();

    public List<Route> getRoutes() {
        return new ArrayList<>(routes.values());
    }

    public Route getRoute(UUID id) {
        return routes.get(id);
    }

    public Route addRoute(Route route) {
        route.setId(UUID.randomUUID());
        routes.put(route.getId(), route);
        return route;
    }

    public Route updateRoute(UUID id, Route patch) {
        var route = routes.get(id);
        if (route == null) {
            return null;
        }
        if (patch.getDate() != null) route.setDate(patch.getDate());
        if (patch.getMaxMass() != null) route.setMaxMass(patch.getMaxMass());
        if (patch.getStatus() != null) route.setStatus(patch.getStatus());
        return route;
    }

    public void deleteRoute(UUID id) {
        stopService.deleteByRouteId(id);
        routes.remove(id);
    }

}