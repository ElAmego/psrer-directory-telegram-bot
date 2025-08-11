package by.psrer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = "routeImageId")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "route_image")
public class RouteImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeImageId;
    private String routeImageUrl;
    private String routeImageFileName;
}