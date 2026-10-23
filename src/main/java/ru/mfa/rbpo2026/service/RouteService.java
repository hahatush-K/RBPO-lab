package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.dto.RouteDto;
import ru.mfa.rbpo2026.entity.Route;
import ru.mfa.rbpo2026.repository.RouteRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ru.mfa.rbpo2026.service.Errors.badRequest;
import static ru.mfa.rbpo2026.service.Errors.notFound;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routes;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<RouteDto> getAll() {
        return routes.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public RouteDto get(UUID id) {
        return mapper.toDto(requireRoute(id));
    }

    @Transactional
    public RouteDto create(RouteDto dto) {
        if (dto.getDate() == null) {
            throw badRequest("Не указана дата маршрута (date)");
        }
        checkMaxMass(dto.getMaxMass());
        var route = new Route();
        route.setDate(dto.getDate());
        route.setMaxMass(dto.getMaxMass());
        if (dto.getStatus() != null) {
            route.setStatus(dto.getStatus());
        }
        return mapper.toDto(routes.save(route));
    }

    @Transactional
    public RouteDto update(UUID id, RouteDto patch) {
        var route = requireRoute(id);
        if (patch.getDate() != null) {
            route.setDate(patch.getDate());
        }
        if (patch.getMaxMass() != null) {
            checkMaxMass(patch.getMaxMass());
            route.setMaxMass(patch.getMaxMass());
        }
        if (patch.getStatus() != null) {
            route.setStatus(patch.getStatus());
        }
        return mapper.toDto(route);
    }

    @Transactional
    public void delete(UUID id) {
        routes.delete(requireRoute(id));
    }

    private void checkMaxMass(BigDecimal maxMass) {
        if (maxMass == null || maxMass.signum() <= 0) {
            throw badRequest("Ограничение массы (maxMass) должно быть положительным числом");
        }
    }

    private Route requireRoute(UUID id) {
        return routes.findById(id).orElseThrow(() -> notFound("Маршрут не найден"));
    }
}
