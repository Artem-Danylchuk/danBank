package Bank.backBank.services;

import Bank.backBank.entity.ClientsLogin;

public interface ClientsService {

    void save(ClientsLogin clientsLogin);

    ClientsLogin findByLogin (String login);
}
