package Bank.backBank.services;

import Bank.backBank.entity.ClientsLogin;
import Bank.backBank.entity.UserRole;
import Bank.backBank.repository.ClientsLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClientServiceImpl implements ClientsService{
    @Autowired
    private ClientsLoginRepository clientsLoginRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public void save(ClientsLogin clientsLogin) {
        clientsLogin.setPassword(bCryptPasswordEncoder.encode(clientsLogin.getPassword()));
        Set<UserRole> roles = new HashSet<>();
        clientsLogin.setRole(UserRole.USER);
        clientsLoginRepository.save(clientsLogin);
    }

    @Override
    public ClientsLogin findByLogin(String login) {
        return clientsLoginRepository.findByLogin(login);
    }
}
