package Bank.backBank.repository;

import Bank.backBank.entity.ClientsInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientsInformationRepository extends JpaRepository<ClientsInformation, Long> {


    @Override
    Optional<ClientsInformation> findById(Long aLong);

}
