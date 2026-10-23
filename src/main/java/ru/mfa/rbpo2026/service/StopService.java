package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.dto.StopDto;
import ru.mfa.rbpo2026.entity.Route;
import ru.mfa.rbpo2026.entity.Stop;
import ru.mfa.rbpo2026.repository.RouteRepository;
import ru.mfa.rbpo2026.repository.StopRepository;

import java.util.List;
import java.util.UUID;

import static ru.mfa.rbpo2026.service.Errors.badRequest;
import static ru.mfa.rbpo2026.service.Errors.notFound;

@Service
@RequiredArgsConstructor
public class StopService {
    private final StopRepository stops;
    private final RouteRepository routes;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<StopDto> getAll() {
        return stops.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StopDto get(UUID id) {
        return mapper.toDto(requireStop(id));
    }

    @Transactional
    public StopDto create(StopDto dto) {
        if (dto.getRouteId() == null) {
            throw badRequest("Не указан маршрут (routeId)");
        }
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw badRequest("Не указан адрес (address)");
        }
        checkOrderNumber(dto.getOrderNumber());
        var stop = new Stop();
        stop.setRoute(requireRoute(dto.getRouteId()));
        stop.setAddress(dto.getAddress());
        stop.setOrderNumber(dto.getOrderNumber());
        return mapper.toDto(stops.save(stop));
    }

    @Transactional
    public StopDto update(UUID id, StopDto patch) {
        var stop = requireStop(id);
        if (patch.getRouteId() != null) {
            stop.setRoute(requireRoute(patch.getRouteId()));
        }
        if (patch.getAddress() != null) {
            if (patch.getAddress().isBlank()) {
                throw badRequest("Адрес не может быть пустым");
            }
            stop.setAddress(patch.getAddress());
        }
        if (patch.getOrderNumber() != null) {
            checkOrderNumber(patch.getOrderNumber());
            stop.setOrderNumber(patch.getOrderNumber());
        }
        return mapper.toDto(stop);
    }

    @Transactional
    public void delete(UUID id) {
        stops.delete(requireStop(id));
    }

    private void checkOrderNumber(Integer orderNumber) {
        if (orderNumber == null || orderNumber <= 0) {
            throw badRequest("Порядковый номер (orderNumber) должен быть положительным числом");
        }
    }

    private Stop requireStop(UUID id) {
        return stops.findById(id).orElseThrow(() -> notFound("Остановка не найдена"));
    }

    private Route requireRoute(UUID id) {
        return routes.findById(id).orElseThrow(() -> notFound("Маршрут не найден"));
    }
}
