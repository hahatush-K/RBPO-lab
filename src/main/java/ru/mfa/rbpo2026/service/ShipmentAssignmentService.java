package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.dto.ShipmentAssignmentDto;
import ru.mfa.rbpo2026.entity.Shipment;
import ru.mfa.rbpo2026.entity.ShipmentAssignment;
import ru.mfa.rbpo2026.entity.Stop;
import ru.mfa.rbpo2026.repository.ShipmentAssignmentRepository;
import ru.mfa.rbpo2026.repository.ShipmentRepository;
import ru.mfa.rbpo2026.repository.StopRepository;

import java.util.List;
import java.util.UUID;

import static ru.mfa.rbpo2026.service.Errors.badRequest;
import static ru.mfa.rbpo2026.service.Errors.notFound;

@Service
@RequiredArgsConstructor
public class ShipmentAssignmentService {
    private final ShipmentAssignmentRepository assignments;
    private final ShipmentRepository shipments;
    private final StopRepository stops;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<ShipmentAssignmentDto> getAll() {
        return assignments.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ShipmentAssignmentDto get(UUID id) {
        return mapper.toDto(requireAssignment(id));
    }

    @Transactional
    public ShipmentAssignmentDto create(ShipmentAssignmentDto dto) {
        if (dto.getShipmentId() == null) {
            throw badRequest("Не указано отправление (shipmentId)");
        }
        if (dto.getStopId() == null) {
            throw badRequest("Не указана остановка (stopId)");
        }
        var assignment = new ShipmentAssignment();
        assignment.setShipment(requireShipment(dto.getShipmentId()));
        assignment.setStop(requireStop(dto.getStopId()));
        if (dto.getStatus() != null) {
            assignment.setStatus(dto.getStatus());
        }
        return mapper.toDto(assignments.save(assignment));
    }

    @Transactional
    public ShipmentAssignmentDto update(UUID id, ShipmentAssignmentDto patch) {
        var assignment = requireAssignment(id);
        if (patch.getShipmentId() != null) {
            assignment.setShipment(requireShipment(patch.getShipmentId()));
        }
        if (patch.getStopId() != null) {
            assignment.setStop(requireStop(patch.getStopId()));
        }
        if (patch.getStatus() != null) {
            assignment.setStatus(patch.getStatus());
        }
        return mapper.toDto(assignment);
    }

    @Transactional
    public void delete(UUID id) {
        assignments.delete(requireAssignment(id));
    }

    private ShipmentAssignment requireAssignment(UUID id) {
        return assignments.findById(id).orElseThrow(() -> notFound("Назначение не найдено"));
    }

    private Shipment requireShipment(UUID id) {
        return shipments.findById(id).orElseThrow(() -> notFound("Отправление не найдено"));
    }

    private Stop requireStop(UUID id) {
        return stops.findById(id).orElseThrow(() -> notFound("Остановка не найдена"));
    }
}
