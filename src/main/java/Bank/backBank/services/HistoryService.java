package Bank.backBank.services;

import Bank.backBank.entity.ClientsHistory;
import Bank.backBank.repository.ClientsHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoryService {
    private final ClientsHistoryRepository clientsHistoryRepository;

    public HistoryService(ClientsHistoryRepository clientsHistoryRepository) {
        this.clientsHistoryRepository = clientsHistoryRepository;
    }

    @Transactional(readOnly = true)
    public long count() {
        return clientsHistoryRepository.count();
    }
    @Transactional(readOnly = true)
    public Page<ClientsHistory> getEntities(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return clientsHistoryRepository.findAll(pageRequest);
    }
}
