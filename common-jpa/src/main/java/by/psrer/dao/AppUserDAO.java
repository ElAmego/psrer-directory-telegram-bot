package by.psrer.dao;

import by.psrer.entity.AppUser;
import by.psrer.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(final Long userId);
    List<AppUser> findAllByRole(final Role role);
}
