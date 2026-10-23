package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.rbpo2026.dto.UserDto;
import ru.mfa.rbpo2026.entity.User;
import ru.mfa.rbpo2026.repository.UserRepository;

import java.util.List;
import java.util.UUID;

import static ru.mfa.rbpo2026.service.Errors.badRequest;
import static ru.mfa.rbpo2026.service.Errors.notFound;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository users;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return users.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto get(UUID id) {
        return mapper.toDto(requireUser(id));
    }

    @Transactional
    public UserDto create(UserDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw badRequest("Не указано имя пользователя (name)");
        }
        var user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail() == null || dto.getEmail().isBlank() ? null : dto.getEmail());
        return mapper.toDto(users.save(user));
    }

    @Transactional
    public UserDto update(UUID id, UserDto patch) {
        var user = requireUser(id);
        if (patch.getName() != null) {
            if (patch.getName().isBlank()) {
                throw badRequest("Имя пользователя не может быть пустым");
            }
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null) {
            user.setEmail(patch.getEmail().isBlank() ? null : patch.getEmail());
        }
        return mapper.toDto(user);
    }

    @Transactional
    public void delete(UUID id) {
        users.delete(requireUser(id));
    }

    private User requireUser(UUID id) {
        return users.findById(id).orElseThrow(() -> notFound("Пользователь не найден"));
    }
}
