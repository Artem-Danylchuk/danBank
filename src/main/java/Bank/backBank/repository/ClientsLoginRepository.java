package Bank.backBank.repository;

import Bank.backBank.entity.ClientsLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientsLoginRepository extends JpaRepository<ClientsLogin, Long> {
    @Query("SELECT u FROM ClientsLogin u where u.login = :login")
    ClientsLogin findByLogin(@Param("login") String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM ClientsLogin u where u.email = :email")
    ClientsLogin findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(e) FROM ClientsLogin e")
    Long getAll();
}
