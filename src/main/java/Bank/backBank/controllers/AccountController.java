package Bank.backBank.controllers;

import Bank.backBank.entity.*;
import Bank.backBank.repository.ClientsAccountRepository;
import Bank.backBank.repository.ClientsHistoryRepository;
import Bank.backBank.repository.ClientsInformationRepository;
import Bank.backBank.repository.RatesRepository;
import Bank.backBank.services.AccountService;
import Bank.backBank.services.HistoryService;
import Bank.backBank.services.TransactionService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final ClientsAccountRepository clientsAccountRepository;
    private final AccountService accountService;
    private final ClientsInformationRepository clientsInformationRepository;
    private final ClientsHistoryRepository clientsHistoryRepository;
    private final RatesRepository ratesRepository;
    private final HistoryService historyService;
    private final TransactionService transactionService;
    static final int DEFAULT_GROUP_ID = -1;
    static final int ITEMS_PER_PAGE = 4;

    @GetMapping("/cardsPage")
    public String index1(Model model) {
        User user = SecurityUtils.getCurrentUser();

        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);

        model.addAttribute("login", login);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("cardNumberEur", formatLongWithSpaces(dbUser.getClientsAccount().getCardEur()));
        model.addAttribute("cardNumberUah", formatLongWithSpaces(dbUser.getClientsAccount().getCardUah()));
        model.addAttribute("cardNumberUsd", formatLongWithSpaces(dbUser.getClientsAccount().getCardUsd()));
        model.addAttribute("cvvCartEur", dbUser.getClientsAccount().getCvvCardEur());
        model.addAttribute("cvvCartUsd", dbUser.getClientsAccount().getCvvCardUsd());
        model.addAttribute("cvvCartUah", dbUser.getClientsAccount().getCvvCardUah());
        model.addAttribute("name", dbUser.getClientsInformation().getName());
        model.addAttribute("surname", dbUser.getClientsInformation().getSurname());
        model.addAttribute("currencyUah", dbUser.getClientsAccount().getCurrencyUah());
        model.addAttribute("currencyEur", dbUser.getClientsAccount().getCurrencyEur());
        model.addAttribute("currencyUsd", dbUser.getClientsAccount().getCurrencyUsd());

        return "accountPage/cardsPage";
    }

    @GetMapping("myAccount")
    public String myAccount(Model model) {
        User user = SecurityUtils.getCurrentUser();

        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);


        model.addAttribute("login", login);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("name", dbUser.getClientsInformation().getName());
        model.addAttribute("surname", dbUser.getClientsInformation().getSurname());
        model.addAttribute("number", dbUser.getClientsInformation().getNumber());
        model.addAttribute("country", dbUser.getClientsInformation().getCountry());
        model.addAttribute("city", dbUser.getClientsInformation().getCity());
        model.addAttribute("street", dbUser.getClientsInformation().getStreet());
        model.addAttribute("zipCode", dbUser.getClientsInformation().getZipCode());


        return "accountPage/myAccount";
    }

    private static String formatLongWithSpaces(Long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,####");
        return decimalFormat.format(number);
    }

    @PostMapping(value = "/search")
    public String searchByCard(@RequestParam(name = "searchCardNumber", required = false) String searchCardNumber,
                               @RequestParam(name = "sum") String sum,
                               @RequestParam("selectedCard") String selectedCard, Model model) {
        Long cardNumberSearch = 0L;
        if (searchCardNumber.length() < 6 || searchCardNumber.length() > 18) {
            model.addAttribute("errorLength", "Enter the correct card number.");
            return "accountPage/transfers";
        }
        try {
            cardNumberSearch = Long.parseLong(searchCardNumber);
        } catch (Exception e) {
            model.addAttribute("errorString", "You need to write only the card number.");
            return "accountPage/transfers";
        }
        Long id = clientsAccountRepository.findIdsByColumnsContaining(cardNumberSearch);
        if (id == null) {
            model.addAttribute("cardNumberNo", "Such a card does not exist");
            return "accountPage/transfers";
        }

        ClientsInformation clientsInformation = accountService.getClientsInformationById(id);
        model.addAttribute("cardNumberYes", "Perfect! Check the recipient's details.");
        model.addAttribute("accountInf", clientsInformation);
        model.addAttribute("cardNumberFind", formatLongWithSpaces(cardNumberSearch));
        model.addAttribute("lastSearchValue", searchCardNumber);
        model.addAttribute("sum", sum);
        clientsInformationInTransfer(model);

        Double sumDouble = Double.parseDouble(sum);
        Double sumWithPercent = sumDouble + (sumDouble * (5.0 / 100.0));
        model.addAttribute("sumWithPercent", sumWithPercent);
        model.addAttribute("selectCard", addCardNumberBySelectCard(selectedCard));
        Long card = addCardNumberBySelectCard(selectedCard);
        Long recipientCard = Long.parseLong(searchCardNumber);


        try {
            String resultTest = transactionService.transferMoney(card, sumDouble, recipientCard);
            model.addAttribute("result", resultTest);
            return "accountPage/transfers";
        } catch (Exception e) {
            return "accountPage/transfers";
        }


    }

    @GetMapping(value = "/transfers")
    public String clientsInformationInTransfer(Model model) {
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);

        model.addAttribute("login", login);
        model.addAttribute("cardEuroL", dbUser.getClientsAccount().getCardEur());
        model.addAttribute("cardUahL", dbUser.getClientsAccount().getCardUah());
        model.addAttribute("cardUsdL", dbUser.getClientsAccount().getCardUsd());
        model.addAttribute("sumCardEur", dbUser.getClientsAccount().getEur());
        model.addAttribute("sumCardUah", dbUser.getClientsAccount().getUah());
        model.addAttribute("sumCardUsd", dbUser.getClientsAccount().getUsd());
        return "accountPage/transfers";
    }

    private Long addCardNumberBySelectCard(String selectCard) {
        String regex = ": (\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(selectCard);
        if (matcher.find()) {
            String cardNumberString = matcher.group(1);
            try {
                Long cardNumber = Long.parseLong(cardNumberString);
                return cardNumber;
            } catch (NumberFormatException e) {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    @GetMapping(value = "/exchange")
    public String exchange(Model model) {
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);

        Rates rates = ratesRepository.getReferenceById(1L);
        Rates rates2 = ratesRepository.getReferenceById(2L);
        Rates rates3 = ratesRepository.getReferenceById(3L);
        Rates rates4 = ratesRepository.getReferenceById(4L);
        Rates rates5 = ratesRepository.getReferenceById(5L);
        Rates rates6 = ratesRepository.getReferenceById(6L);

        Double eurToUah = rates.getExchangeRate();
        Double eurToUsd = rates2.getExchangeRate();
        Double usdToUah = rates3.getExchangeRate();
        Double usdToEur = rates4.getExchangeRate();
        Double uahToUsd = rates5.getExchangeRate();
        Double uahToEur = rates6.getExchangeRate();

        model.addAttribute("login", login);
        model.addAttribute("eurToUah", eurToUah);
        model.addAttribute("eurToUsd", eurToUsd);
        model.addAttribute("usdToUah", usdToUah);
        model.addAttribute("usdToEur", usdToEur);
        model.addAttribute("uahToUsd", uahToUsd);
        model.addAttribute("uahToEur", uahToEur);

        model.addAttribute("cardEuroL", dbUser.getClientsAccount().getCardEur());
        model.addAttribute("cardUahL", dbUser.getClientsAccount().getCardUah());
        model.addAttribute("cardUsdL", dbUser.getClientsAccount().getCardUsd());
        model.addAttribute("sumCardEur", dbUser.getClientsAccount().getEur());
        model.addAttribute("sumCardUah", dbUser.getClientsAccount().getUah());
        model.addAttribute("sumCardUsd", dbUser.getClientsAccount().getUsd());


        return "accountPage/exchange";
    }

    @PostMapping(value = "/exchangePost")
    public String exchange(@RequestParam(name = "fromCard") String fromCard,
                           @RequestParam(name = "sum") String sum,
                           @RequestParam("toCard") String toCard, Model model) {
        model.addAttribute("sum", sum);

        Double sumD = Double.parseDouble(sum);
        Long card = addCardNumberBySelectCard(toCard);
        Long fCard = addCardNumberBySelectCard(fromCard);
        try {
            String resultTest = transactionService.exchangeOperation(fCard, sumD, card);
            model.addAttribute("result", resultTest);
            return "accountPage/exchange";
        } catch (Exception e) {
            return "accountPage/exchange";
        }


    }

    @GetMapping(value = "/history")
    public String historyGet(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);

        model.addAttribute("login", login);

        Page<ClientsHistory> historyPage = clientsHistoryRepository.findAllByClientsLoginId(
                dbUser.getId(),
                PageRequest.of(page, 12, Sort.by(Sort.Order.desc("id")))
        );
        model.addAttribute("historyPage", historyPage);
        model.addAttribute("numbers", IntStream.range(0, historyPage.getTotalPages()).toArray());

        return "accountPage/history";
    }

    @GetMapping("/historyInformation/{id}")
    public String viewDetails(@PathVariable Long id, Model model) {
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();

        ClientsInformation clientsInf = accountService.getClientsInformationById(id);
        ClientsAccount clientsAccount = accountService.getClientsAccountById(id);
        ClientsHistory clientsHistory = accountService.getClientsHistoryById(id);

        model.addAttribute("login", login);
        model.addAttribute("clientsInformation", clientsInf);
        model.addAttribute("clientsAccount", clientsAccount);
        model.addAttribute("clientsHistory", clientsHistory);

        return "accountPage/historyInformation";
    }

    @GetMapping(value = "/system")
    public String systemGet(Model model) {
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();
        ClientsLogin dbUser = userService.findByLogin(login);
        model.addAttribute("login", login);

        return "accountPage/system";
    }

    @PostMapping(value = "/system")
    public String systemPost(
            @RequestParam String selectedField,
            @RequestParam String fieldValue, Model model) {
        User user = SecurityUtils.getCurrentUser();
        String login = user.getUsername();

        ClientsLogin dbUser = userService.findByLogin(login);
        ClientsInformation clientsInformation = accountService.getClientsInformationById(dbUser.getId());

        if (clientsInformation != null) {
            switch (selectedField) {
                case "country":
                    clientsInformation.setCountry(fieldValue);
                    break;
                case "city":
                    clientsInformation.setCity(fieldValue);
                    break;
                case "street":
                    clientsInformation.setStreet(fieldValue);
                    break;
                case "zipCode":
                    clientsInformation.setZipCode(fieldValue);
                    break;
                case "number":
                   // Integer number = Integer.parseInt(fieldValue);
                    clientsInformation.setNumber(fieldValue);
                    break;
                case "name":
                    clientsInformation.setName(fieldValue);
                    break;
                case "surname":
                    clientsInformation.setSurname(fieldValue);
                    break;
                case "secretWord":
                    clientsInformation.setSecretWord(fieldValue);
                    break;
            }
            clientsInformationRepository.save(clientsInformation);

        }
        model.addAttribute("result", "Greate!");
        return "accountPage/system";
    }

    private long getPageCount() {
        long totalCount = historyService.count();
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }


}
