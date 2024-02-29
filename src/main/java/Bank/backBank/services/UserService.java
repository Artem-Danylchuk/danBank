package Bank.backBank.services;


import Bank.backBank.dto.AddInformationUserDTO;
import Bank.backBank.dto.CreateUserDTO;
import Bank.backBank.entity.ClientsAccount;
import Bank.backBank.entity.ClientsInformation;
import Bank.backBank.entity.ClientsLogin;
import Bank.backBank.entity.UserRole;
import Bank.backBank.repository.ClientsAccountRepository;
import Bank.backBank.repository.ClientsHistoryRepository;
import Bank.backBank.repository.ClientsInformationRepository;
import Bank.backBank.repository.ClientsLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EntityManager entityManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ClientsLoginRepository clientsLoginRepository;
    private final ClientsAccountRepository clientsAccountRepository;
    private final ClientsHistoryRepository clientsHistoryRepository;
    private final ClientsInformationRepository clientsInformationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<ClientsLogin> getAllUsers() {
        return clientsLoginRepository.findAll();
    }
    @Transactional(readOnly = true)
    public ClientsLogin findByLogin(String login) {
        return clientsLoginRepository.findByLogin(login);
    }
    @Transactional(readOnly = true)
    public ClientsLogin findByEmail(String email) {
        return clientsLoginRepository.findByLogin(email);
    }
    @Transactional(readOnly = true)
    public ClientsLogin findInformationByLogin(String login) {
        return clientsLoginRepository.findByLogin(login);
    }
    @Transactional
    public ClientsLogin addUser(CreateUserDTO createUser) {

        return addUserWithRole(createUser.getLogin(), createUser.getPassword(), createUser.getEmail(), UserRole.USER);
    }
    @Transactional
    public ClientsLogin addAdmin(String login, String password, String email) {
        return addUserWithRole(login, password, email, UserRole.ADMIN);
    }
    @Transactional
    private ClientsLogin addUserWithRole(String login, String password, String email, UserRole role) {
        String passHash = passwordEncoder.encode(password);

        ClientsLogin user = new ClientsLogin(login, passHash, email, role);

        ClientsInformation clientsInformation = new ClientsInformation("-", "-", "-", "-", "-", "-", "-", "-");
        user.setClientsInformation(clientsInformation);

        ClientsAccount clientsAccount = new ClientsAccount(100.0, createVisaCardNumber(),createCvvForCards(),currencyDate(), 100.0, createVisaCardNumber(),createCvvForCards(),currencyDate(), 100.0, createMasterCardNumber(),createCvvForCards(),currencyDate());
        user.setClientsAccount(clientsAccount);



        return clientsLoginRepository.save(user);
    }
    @Transactional
    public ClientsLogin addInformation(String login, AddInformationUserDTO addInfUserDTO) {
        return addClientsInformation(login, addInfUserDTO.getName(), addInfUserDTO.getSurname(), addInfUserDTO.getSecretWord(),
                addInfUserDTO.getNumber(), addInfUserDTO.getStreet(), addInfUserDTO.getCountry(), addInfUserDTO.getCity(), addInfUserDTO.getZipCode());
    }
    @Transactional
    private ClientsLogin addClientsInformation(String login, String name, String surname, String secretWord, String number, String street, String country, String city, String zipCode) {
        ClientsLogin clientsLogin = clientsLoginRepository.findByLogin(login);
        ClientsInformation clientsInformation = clientsLogin.getClientsInformation();

        clientsInformation.setName(name);
        clientsInformation.setSurname(surname);
        clientsInformation.setSecretWord(secretWord);
        clientsInformation.setNumber(number);
        clientsInformation.setStreet(street);
        clientsInformation.setCountry(country);
        clientsInformation.setCity(city);
        clientsInformation.setZipCode(zipCode);
        clientsLoginRepository.save(clientsLogin);


        return clientsLogin;
    }
    @Transactional
    public void updateInformation(String login,
                                  String country,
                                  String city,
                                  String street,
                                  String zipCode,
                                  String name,
                                  String surname,
                                  String number) {
        ClientsLogin clientsLogin = clientsLoginRepository.findByLogin(login);
        if (clientsLogin == null)
            return;

        ClientsInformation clientsInformation = clientsLogin.getClientsInformation();
        clientsInformation.setCountry(country);
        clientsInformation.setCity(city);
        clientsInformation.setStreet(street);
        clientsInformation.setZipCode(zipCode);
        clientsInformation.setName(name);
        clientsInformation.setSurname(surname);
        clientsInformation.setNumber(number);

        clientsLoginRepository.save(clientsLogin);
    }

    private Long createVisaCardNumber() {

        Random random = new Random();
        int firstPart = 59593030;

        int randomNumber = random.nextInt(1000000);
        String formattedNumber = String.format("%08d", randomNumber);

        String card = firstPart + formattedNumber;
        Long finish = Long.parseLong(card);

        ClientsAccount cl = clientsAccountRepository.findByCardEur(finish);
        ClientsAccount cl2 = clientsAccountRepository.findByCardUah(finish);


        if (cl != null || cl2 != null ) {
            return createVisaCardNumber();
        } else {
            return finish;
        }

}
    private Long createMasterCardNumber() {
        Random random = new Random();
        int firstPart = 87873030;

        int randomNumber = random.nextInt(1000000);
        String formattedNumber = String.format("%08d", randomNumber);

        String card = firstPart + formattedNumber;
        Long finish = Long.parseLong(card);

        ClientsAccount cl3 = clientsAccountRepository.findByCardUsd(finish);

        if (cl3 != null) {
            return createMasterCardNumber();
        } else {
            return finish;
        }

    }
    private Long createCvvForCards(){
        Random random = new Random();
        Long cod = random.nextLong(900)+100;
        return cod;
    }

    private LocalDate currencyDate (){
        LocalDate currentDate = LocalDate.now();
        return currentDate.plusYears(5);
    }






}
