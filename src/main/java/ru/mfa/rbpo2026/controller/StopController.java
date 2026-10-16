package ru.mfa.rbpo2026.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.rbpo2026.entity.Stop;
import ru.mfa.rbpo2026.service.StopService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stops")
public class StopController {

    private final StopService stopService;

    @GetMapping
    public ResponseEntity<List<Stop>> getStops() {
        return ResponseEntity.ok(stopService.getStops());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stop> getStop(@PathVariable UUID id) {
        var stop = stopService.getStop(id);
        if (stop == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(stop);
    }

    @PostMapping
    public ResponseEntity<Stop> addStop(@RequestBody Stop stop) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stopService.addStop(stop));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Stop> updateStop(@PathVariable UUID id, @RequestBody Stop stop) {
        var updated = stopService.updateStop(id, stop);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStop(@PathVariable UUID id) {
        stopService.deleteStop(id);
        return ResponseEntity.noContent().build();
    }

}