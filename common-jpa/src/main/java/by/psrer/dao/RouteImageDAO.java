package by.psrer.dao;

import by.psrer.entity.RouteImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteImageDAO extends JpaRepository<RouteImage, Long> {
    RouteImage findRouteImageByRouteImageId(final Long routeImageId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RouteImage r WHERE r.routeImageId = :routeImageId")
    void deleteRouteImageByRouteImageId(@Param("routeImageId") final Long routeImageId);
}