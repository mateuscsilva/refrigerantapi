package one.digitalinnovation.refrigerantapi.repository;

import one.digitalinnovation.refrigerantapi.entity.Soda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SodaRepository extends JpaRepository<Soda, Long> {

    Optional<Soda> findByName(String name);
}
