package ru.mfa.rbpo2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mfa.rbpo2026.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
