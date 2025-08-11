package by.psrer.dao;

import by.psrer.entity.Question;
import by.psrer.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteDAO extends JpaRepository<Route, Long> {
    List<Route> findAll();
    Route findRouteByRouteId(final Long routeId);

    @Query(value = "SELECT * FROM route ORDER BY route_id LIMIT 1 OFFSET :offset", nativeQuery = true)
    Route findNth(@Param("offset") final int offset);

    // Доп. метод для безопасного получения (возвращает Optional)
    default Optional<Route> findNthSafely(final int n) {
        if (n <= 0) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(findNth(n - 1)); // преобразуем в 0-based offset
        } catch (Exception e) {
            return Optional.empty(); // если запись не найдена или ошибка БД
        }
    }
}
