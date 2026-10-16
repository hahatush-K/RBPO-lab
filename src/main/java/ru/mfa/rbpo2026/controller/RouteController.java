package ru.mfa.rbpo2026.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.rbpo2026.entity.Route;
import ru.mfa.rbpo2026.service.RouteService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<List<Route>> getRoutes() {
        return ResponseEntity.ok(routeService.getRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRoute(@PathVariable UUID id) {
        var route = routeService.getRoute(id);
        if (route == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(route);
    }

    @PostMapping
    public ResponseEntity<Route> addRoute(@RequestBody Route route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.addRoute(route));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable UUID id, @RequestBody Route route) {
        var updated = routeService.updateRoute(id, route);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable UUID id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

}