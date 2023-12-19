package Bank.backBank.repository;

import Bank.backBank.entity.ClientsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientsAccountRepository extends JpaRepository<ClientsAccount, Long> {

    ClientsAccount findByCardEur(Long cardNumber);

    ClientsAccount findByCardUah(Long cardNumber);

    ClientsAccount findByCardUsd(Long cardNumber);

    @Override
    List<ClientsAccount> findAll();

    @Query("SELECT ca.id " +
            "FROM ClientsAccount ca " +
            "WHERE LOWER(ca.cardEur) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(ca.cardUah) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(ca.cardUsd) LIKE LOWER(CONCAT('%', :query, '%'))")
    Long findIdsByColumnsContaining(@Param("query") Long query);

    @Override
    Optional<ClientsAccount> findById(Long aLong);
}
