package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.dto.RouteDto;
import ru.mfa.rbpo2026.dto.ShipmentAssignmentDto;
import ru.mfa.rbpo2026.entity.AssignmentStatus;
import ru.mfa.rbpo2026.entity.Route;
import ru.mfa.rbpo2026.entity.RouteStatus;
import ru.mfa.rbpo2026.entity.Shipment;
import ru.mfa.rbpo2026.entity.ShipmentAssignment;
import ru.mfa.rbpo2026.entity.ShipmentStatus;
import ru.mfa.rbpo2026.entity.Stop;
import ru.mfa.rbpo2026.repository.RouteRepository;
import ru.mfa.rbpo2026.repository.ShipmentAssignmentRepository;
import ru.mfa.rbpo2026.repository.ShipmentRepository;
import ru.mfa.rbpo2026.repository.StopRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static ru.mfa.rbpo2026.service.Errors.badRequest;
import static ru.mfa.rbpo2026.service.Errors.conflict;
import static ru.mfa.rbpo2026.service.Errors.notFound;

/**
 * Бизнес-операции доставки. Каждая операция выполняется в одной транзакции:
 * либо все изменения применяются, либо (при ошибке) не применяется ни одно.
 * Сначала блокируется строка маршрута, и только потом читаются остальные данные,
 * поэтому две операции над одним маршрутом не могут выполняться одновременно.
 */
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final ShipmentRepository shipments;
    private final StopRepository stops;
    private final RouteRepository routes;
    private final ShipmentAssignmentRepository assignments;
    private final DtoMapper mapper;

    /**
     * 1. Назначить отправление на остановку.
     * Отправление должно быть NEW, маршрут остановки PLANNED,
     * а суммарная масса назначенных отправлений не должна превысить ограничение маршрута.
     * Результат: создаётся назначение PENDING, отправление становится ASSIGNED.
     */
    @Transactional
    public ShipmentAssignmentDto assignShipment(UUID shipmentId, UUID stopId) {
        if (stopId == null) {
            throw badRequest("Не указана остановка (stopId)");
        }
        var routeId = stops.findRouteIdById(stopId).orElseThrow(() -> notFound("Остановка не найдена"));
        var route = lockRoute(routeId);
        var shipment = shipments.findById(shipmentId).orElseThrow(() -> notFound("Отправление не найдено"));
        Stop stop = stops.findById(stopId).orElseThrow(() -> notFound("Остановка не найдена"));

        if (shipment.getStatus() != ShipmentStatus.NEW) {
            throw conflict("Назначить можно только отправление в статусе NEW, сейчас: " + shipment.getStatus());
        }
        if (route.getStatus() != RouteStatus.PLANNED) {
            throw conflict("Назначать отправления можно только на маршрут в статусе PLANNED, сейчас: " + route.getStatus());
        }
        BigDecimal load = activeMass(routeId);
        if (load.add(shipment.getMass()).compareTo(route.getMaxMass()) > 0) {
            throw conflict("Превышено ограничение массы маршрута: занято " + load + " из " + route.getMaxMass()
                    + ", масса отправления " + shipment.getMass());
        }

        var assignment = new ShipmentAssignment();
        assignment.setShipment(shipment);
        assignment.setStop(stop);
        assignment.setStatus(AssignmentStatus.PENDING);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
        return mapper.toDto(assignments.save(assignment));
    }

    /**
     * 2. Снять назначение.
     * Допустимо, пока назначение PENDING и маршрут PLANNED.
     * Результат: назначение CANCELLED, отправление снова NEW и может быть назначено заново.
     */
    @Transactional
    public ShipmentAssignmentDto unassign(UUID assignmentId) {
        var routeId = assignments.findRouteIdById(assignmentId).orElseThrow(() -> notFound("Назначение не найдено"));
        var route = lockRoute(routeId);
        var assignment = requireAssignment(assignmentId);

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw conflict("Снять можно только назначение в статусе PENDING, сейчас: " + assignment.getStatus());
        }
        if (route.getStatus() != RouteStatus.PLANNED) {
            throw conflict("Снять назначение можно только пока маршрут в статусе PLANNED, сейчас: " + route.getStatus());
        }
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.getShipment().setStatus(ShipmentStatus.NEW);
        return mapper.toDto(assignment);
    }

    /**
     * 3. Начать маршрут.
     * Маршрут должен быть PLANNED, его дата не позже сегодняшней (дату определяет сервер),
     * и на нём должно быть хотя бы одно назначение PENDING.
     * Результат: маршрут IN_PROGRESS, все назначенные отправления становятся IN_TRANSIT.
     */
    @Transactional
    public RouteDto startRoute(UUID routeId) {
        var route = lockRoute(routeId);
        if (route.getStatus() != RouteStatus.PLANNED) {
            throw conflict("Начать можно только маршрут в статусе PLANNED, сейчас: " + route.getStatus());
        }
        if (route.getDate().isAfter(LocalDate.now())) {
            throw conflict("Маршрут запланирован на " + route.getDate() + ", начать его раньше этой даты нельзя");
        }
        var pending = assignments.findByStopRouteIdAndStatus(routeId, AssignmentStatus.PENDING);
        if (pending.isEmpty()) {
            throw conflict("На маршруте нет назначенных отправлений");
        }
        for (ShipmentAssignment assignment : pending) {
            assignment.getShipment().setStatus(ShipmentStatus.IN_TRANSIT);
        }
        route.setStatus(RouteStatus.IN_PROGRESS);
        return mapper.toDto(route);
    }

    /**
     * 4. Отметить доставку.
     * Назначение должно быть PENDING, а маршрут IN_PROGRESS.
     * Результат: назначение и отправление DELIVERED; если на маршруте не осталось
     * недоставленных назначений, маршрут автоматически становится COMPLETED.
     */
    @Transactional
    public ShipmentAssignmentDto deliver(UUID assignmentId) {
        var routeId = assignments.findRouteIdById(assignmentId).orElseThrow(() -> notFound("Назначение не найдено"));
        var route = lockRoute(routeId);
        var assignment = requireAssignment(assignmentId);

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw conflict("Отметить доставку можно только для назначения в статусе PENDING, сейчас: "
                    + assignment.getStatus());
        }
        if (route.getStatus() != RouteStatus.IN_PROGRESS) {
            throw conflict("Отметить доставку можно только на маршруте в статусе IN_PROGRESS, сейчас: "
                    + route.getStatus());
        }
        assignment.setStatus(AssignmentStatus.DELIVERED);
        assignment.getShipment().setStatus(ShipmentStatus.DELIVERED);
        assignments.flush();

        if (!assignments.existsByStopRouteIdAndStatus(routeId, AssignmentStatus.PENDING)) {
            route.setStatus(RouteStatus.COMPLETED);
        }
        return mapper.toDto(assignment);
    }

    /**
     * 5. Отменить маршрут.
     * Допустимо только для маршрута PLANNED.
     * Результат: маршрут CANCELLED, все его назначения PENDING отменяются,
     * а отправления возвращаются в NEW, чтобы их можно было назначить на другой маршрут.
     */
    @Transactional
    public RouteDto cancelRoute(UUID routeId) {
        var route = lockRoute(routeId);
        if (route.getStatus() != RouteStatus.PLANNED) {
            throw conflict("Отменить можно только маршрут в статусе PLANNED, сейчас: " + route.getStatus());
        }
        var pending = assignments.findByStopRouteIdAndStatus(routeId, AssignmentStatus.PENDING);
        for (ShipmentAssignment assignment : pending) {
            assignment.setStatus(AssignmentStatus.CANCELLED);
            Shipment shipment = assignment.getShipment();
            shipment.setStatus(ShipmentStatus.NEW);
        }
        route.setStatus(RouteStatus.CANCELLED);
        return mapper.toDto(route);
    }

    // Масса всех действующих (не отменённых) назначений маршрута
    private BigDecimal activeMass(UUID routeId) {
        BigDecimal total = BigDecimal.ZERO;
        for (ShipmentAssignment assignment : assignments.findByStopRouteIdAndStatusNot(routeId, AssignmentStatus.CANCELLED)) {
            total = total.add(assignment.getShipment().getMass());
        }
        return total;
    }

    private Route lockRoute(UUID routeId) {
        return routes.findByIdForUpdate(routeId).orElseThrow(() -> notFound("Маршрут не найден"));
    }

    private ShipmentAssignment requireAssignment(UUID id) {
        return assignments.findById(id).orElseThrow(() -> notFound("Назначение не найдено"));
    }
}
