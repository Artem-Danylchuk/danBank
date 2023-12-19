package Bank.backBank.controllers;

import Bank.backBank.entity.ClientsAccount;
import Bank.backBank.entity.ClientsHistory;
import Bank.backBank.entity.ClientsInformation;
import Bank.backBank.entity.ClientsLogin;
import Bank.backBank.repository.ClientsHistoryRepository;
import Bank.backBank.repository.ClientsInformationRepository;
import Bank.backBank.repository.ClientsLoginRepository;
import Bank.backBank.services.AccountService;
import Bank.backBank.services.UserService;
import Bank.backBank.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final ClientsLoginRepository clientsLoginRepository;
    private final UserService userService;
    private final AccountService accountService;
    private final ClientsHistoryRepository clientsHistoryRepository;
    private final ClientsInformationRepository clientsInformationRepository;

    @GetMapping("/admin")
    private String adminPageGet (@RequestParam(name = "page", defaultValue = "0") int page, Model model){
        Long allCustomers = clientsLoginRepository.getAll();
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);


        model.addAttribute("allCustomers",allCustomers);
        model.addAttribute("sumUSD",dbUser.getClientsAccount().getUsd());
        model.addAttribute("sumEUR",dbUser.getClientsAccount().getEur());
        model.addAttribute("sumUAH",dbUser.getClientsAccount().getUah());


        Page<ClientsHistory> historyPage  = clientsHistoryRepository.findAll(PageRequest.of(page, 12, Sort.by(Sort.Order.desc("id"))));
        model.addAttribute("historyPage", historyPage);
        model.addAttribute("numbers", IntStream.range(0,historyPage.getTotalPages()).toArray());


    return "adminPages/admin";
    }

    @GetMapping("/adminAllClients")
    private String adminAllClientsGet ( @RequestParam(name = "page",defaultValue = "0") int page,Model model){
        Page<ClientsLogin> clientsLogins = clientsLoginRepository.findAll(PageRequest.of(page, 12, Sort.by(Sort.Order.desc("id"))));
        List<ClientsInformation> clientsInformations = clientsInformationRepository.findAll(Sort.by(Sort.Order.desc("id")));
        model.addAttribute("clientsLogin",clientsLogins);
        model.addAttribute("clientsInformation",clientsInformations);
        return "adminPages/adminAllClients";
    }

    @GetMapping("/adminDetailsClients/{id}")
    public String viewDetails(@PathVariable Long id, Model model) {

        ClientsInformation clientsInf = accountService.getClientsInformationById(id);
        ClientsAccount clientsAccount = accountService.getClientsAccountById(id);

        model.addAttribute("clientsInformation", clientsInf);
        model.addAttribute("clientsAccount",clientsAccount);

        return "adminPages/adminDetailsClients";
    }

}
