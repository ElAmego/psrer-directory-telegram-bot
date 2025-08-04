package by.psrer.dao;

import by.psrer.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionDAO extends JpaRepository<Question, Integer> {
    List<Question> findAll();

    @Query(value = "SELECT * FROM question ORDER BY question_id LIMIT 1 OFFSET :offset", nativeQuery = true)
    Question findNth(@Param("offset") int offset);

    // Доп. метод для безопасного получения (возвращает Optional)
    default Optional<Question> findNthSafely(int n) {
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
