package ru.mfa.rbpo2026.service;

import org.springframework.stereotype.Component;
import ru.mfa.rbpo2026.dto.RouteDto;
import ru.mfa.rbpo2026.dto.ShipmentAssignmentDto;
import ru.mfa.rbpo2026.dto.ShipmentDto;
import ru.mfa.rbpo2026.dto.StopDto;
import ru.mfa.rbpo2026.dto.UserDto;
import ru.mfa.rbpo2026.entity.Route;
import ru.mfa.rbpo2026.entity.Shipment;
import ru.mfa.rbpo2026.entity.ShipmentAssignment;
import ru.mfa.rbpo2026.entity.Stop;
import ru.mfa.rbpo2026.entity.User;

@Component
public class DtoMapper {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public ShipmentDto toDto(Shipment shipment) {
        ShipmentDto dto = new ShipmentDto();
        dto.setId(shipment.getId());
        dto.setSenderId(shipment.getSender().getId());
        dto.setAddress(shipment.getAddress());
        dto.setMass(shipment.getMass());
        dto.setStatus(shipment.getStatus());
        return dto;
    }

    public RouteDto toDto(Route route) {
        RouteDto dto = new RouteDto();
        dto.setId(route.getId());
        dto.setDate(route.getDate());
        dto.setMaxMass(route.getMaxMass());
        dto.setStatus(route.getStatus());
        return dto;
    }

    public StopDto toDto(Stop stop) {
        StopDto dto = new StopDto();
        dto.setId(stop.getId());
        dto.setRouteId(stop.getRoute().getId());
        dto.setAddress(stop.getAddress());
        dto.setOrderNumber(stop.getOrderNumber());
        return dto;
    }

    public ShipmentAssignmentDto toDto(ShipmentAssignment assignment) {
        ShipmentAssignmentDto dto = new ShipmentAssignmentDto();
        dto.setId(assignment.getId());
        dto.setShipmentId(assignment.getShipment().getId());
        dto.setStopId(assignment.getStop().getId());
        dto.setStatus(assignment.getStatus());
        return dto;
    }

}
