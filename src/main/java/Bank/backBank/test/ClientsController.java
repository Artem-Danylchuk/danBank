package Bank.backBank.test;

import Bank.backBank.entity.ClientsHistory;
import Bank.backBank.repository.ClientsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientsController {

    private final ClientsHistoryRepository clientsHistoryRepository;

    @Autowired
    public ClientsController(ClientsHistoryRepository clientsHistoryRepository) {
        this.clientsHistoryRepository = clientsHistoryRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientsHistory> getClientDetails(@PathVariable Long id) {
        ClientsHistory clientDetails = clientsHistoryRepository.findById(id).orElse(null);

        if (clientDetails != null) {
            return new ResponseEntity<>(clientDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}