package Bank.backBank.repository;

import Bank.backBank.entity.ClientsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientsHistoryRepository extends JpaRepository<ClientsHistory, Long> {
    Page<ClientsHistory> findAll(Pageable pageable);

    Page<ClientsHistory> findAllByClientsLoginId(Long clientsLoginId, Pageable pageable);

    @Override
    Optional<ClientsHistory> findById(Long aLong);
}
