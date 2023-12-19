package Bank.backBank.repository;

import Bank.backBank.entity.Rates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatesRepository extends JpaRepository<Rates, Long> {
    Optional<Rates> findByCurrency(String currency);

    Optional<Rates> findById(Long id);

}
