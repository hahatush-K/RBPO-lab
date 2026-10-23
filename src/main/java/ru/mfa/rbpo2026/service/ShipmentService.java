package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.dto.ShipmentDto;
import ru.mfa.rbpo2026.entity.Shipment;
import ru.mfa.rbpo2026.entity.User;
import ru.mfa.rbpo2026.repository.ShipmentRepository;
import ru.mfa.rbpo2026.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ru.mfa.rbpo2026.service.Errors.badRequest;
import static ru.mfa.rbpo2026.service.Errors.notFound;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipments;
    private final UserRepository users;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<ShipmentDto> getAll() {
        return shipments.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ShipmentDto get(UUID id) {
        return mapper.toDto(requireShipment(id));
    }

    @Transactional
    public ShipmentDto create(ShipmentDto dto) {
        if (dto.getSenderId() == null) {
            throw badRequest("Не указан отправитель (senderId)");
        }
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw badRequest("Не указан адрес (address)");
        }
        checkMass(dto.getMass());
        var shipment = new Shipment();
        shipment.setSender(requireUser(dto.getSenderId()));
        shipment.setAddress(dto.getAddress());
        shipment.setMass(dto.getMass());
        if (dto.getStatus() != null) {
            shipment.setStatus(dto.getStatus());
        }
        return mapper.toDto(shipments.save(shipment));
    }

    @Transactional
    public ShipmentDto update(UUID id, ShipmentDto patch) {
        var shipment = requireShipment(id);
        if (patch.getSenderId() != null) {
            shipment.setSender(requireUser(patch.getSenderId()));
        }
        if (patch.getAddress() != null) {
            if (patch.getAddress().isBlank()) {
                throw badRequest("Адрес не может быть пустым");
            }
            shipment.setAddress(patch.getAddress());
        }
        if (patch.getMass() != null) {
            checkMass(patch.getMass());
            shipment.setMass(patch.getMass());
        }
        if (patch.getStatus() != null) {
            shipment.setStatus(patch.getStatus());
        }
        return mapper.toDto(shipment);
    }

    @Transactional
    public void delete(UUID id) {
        shipments.delete(requireShipment(id));
    }

    private void checkMass(BigDecimal mass) {
        if (mass == null || mass.signum() <= 0) {
            throw badRequest("Масса (mass) должна быть положительным числом");
        }
    }

    private Shipment requireShipment(UUID id) {
        return shipments.findById(id).orElseThrow(() -> notFound("Отправление не найдено"));
    }

    private User requireUser(UUID id) {
        return users.findById(id).orElseThrow(() -> notFound("Пользователь не найден"));
    }
}
