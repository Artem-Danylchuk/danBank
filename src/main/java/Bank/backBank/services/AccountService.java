package Bank.backBank.services;

import Bank.backBank.entity.ClientsAccount;
import Bank.backBank.entity.ClientsHistory;
import Bank.backBank.entity.ClientsInformation;
import Bank.backBank.repository.ClientsAccountRepository;
import Bank.backBank.repository.ClientsHistoryRepository;
import Bank.backBank.repository.ClientsInformationRepository;
import Bank.backBank.repository.ClientsLoginRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService {
    private final ClientsAccountRepository clientsAccountRepository;
    private final ClientsLoginRepository clientsLoginRepository;
    private final ClientsInformationRepository clientsInformationRepository;
    private final ClientsHistoryRepository clientsHistoryRepository;

    public AccountService(ClientsAccountRepository clientsAccountRepository, ClientsLoginRepository clientsLoginRepository, ClientsInformationRepository clientsInformationRepository, ClientsHistoryRepository clientsHistoryRepository) {
        this.clientsAccountRepository = clientsAccountRepository;
        this.clientsLoginRepository = clientsLoginRepository;
        this.clientsInformationRepository = clientsInformationRepository;
        this.clientsHistoryRepository = clientsHistoryRepository;
    }
    @Transactional(readOnly = true)
    public ClientsInformation getClientsInformationById (Long id){
        Optional<ClientsInformation> optionalClientsInformation = clientsInformationRepository.findById(id);
        return optionalClientsInformation.orElse(null);
    }
    @Transactional(readOnly = true)
    public ClientsAccount getClientsAccountById (Long id){
        Optional<ClientsAccount> optionalClientsAccount = clientsAccountRepository.findById(id);
        return optionalClientsAccount.orElse(null);
    }
    @Transactional(readOnly = true)
    public ClientsHistory getClientsHistoryById (Long id){
        Optional<ClientsHistory> optionalClientsHistory = clientsHistoryRepository.findById(id);
        return optionalClientsHistory.orElse(null);
    }

}
