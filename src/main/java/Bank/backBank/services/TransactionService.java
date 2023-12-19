package Bank.backBank.services;

import Bank.backBank.entity.*;
import Bank.backBank.repository.ClientsAccountRepository;
import Bank.backBank.repository.ClientsHistoryRepository;
import Bank.backBank.repository.ClientsInformationRepository;
import Bank.backBank.repository.RatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static Bank.backBank.entity.Currency.*;
import static Bank.backBank.utils.CurrencyUtils.getCardCurrency;
import static Bank.backBank.utils.CurrencyUtils.round;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserService userService;
    private final ClientsAccountRepository clientsAccountRepository;
    private final AccountService accountService;
    private final ClientsInformationRepository clientsInformationRepository;
    private final ClientsHistoryRepository clientsHistoryRepository;
    private final RatesRepository ratesRepository;
    private final HistoryService historyService;

    public String transferMoney(Long sender, Double sumToSend, Long recipient) {
        // sender
        Long idSender = clientsAccountRepository.findIdsByColumnsContaining(sender);
        ClientsAccount senderAccount = accountService.getClientsAccountById(idSender);
        ClientsInformation senderInfo = accountService.getClientsInformationById(idSender);

        // recipient
        Long idRecipient = clientsAccountRepository.findIdsByColumnsContaining(recipient);
        ClientsAccount recipientAccount = accountService.getClientsAccountById(idRecipient);
        ClientsInformation recipientInfo = accountService.getClientsInformationById(idRecipient);

        // admin
        ClientsAccount bankAccount = accountService.getClientsAccountById(1L);

        double bankCharge = round(sumToSend * (5.0 / 100.0));
        double sumToCharge = round(sumToSend + bankCharge);
        System.out.println("Исходная сумма: " + sumToSend);
        System.out.println("Сумма для отправки: " + sumToSend);
        System.out.println("Сумма для банка: " + bankCharge);
        System.out.println("Сумма для списання: " + sumToCharge);

        Currency senderCurrency = getCardCurrency(senderAccount, sender);
        Currency recipientCurrency = getCardCurrency(recipientAccount, recipient);
        Double rate = getRate(senderCurrency, recipientCurrency);
        System.out.println("new transfer " + senderCurrency + " - " + recipientCurrency);

        if (senderAccount.getValue(senderCurrency) < sumToCharge) {
            System.out.println("Sorry not enough on the bill.");
            return "Sorry, not enough funds on this account.";
        }

        // take from sender
        double newSenderValue = round(senderAccount.getValue(senderCurrency) - sumToCharge);
        senderAccount.setValueToCard(senderCurrency, newSenderValue);
        // add to recipient
        double newRecipientValue = round(recipientAccount.getValue(recipientCurrency) + (sumToSend * rate));
        recipientAccount.setValueToCard(recipientCurrency, newRecipientValue);
        // add charge to bank
        double newBankValue = round(bankAccount.getValue(senderCurrency) + bankCharge);
        bankAccount.setValueToCard(senderCurrency, newBankValue);

        String dataNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ClientsHistory senderEvent = new ClientsHistory();
        senderEvent.setData(dataNow);
        senderEvent.setCardNumberSender(sender);
        senderEvent.setRecipientName(recipientInfo.getName());
        senderEvent.setRecipientSurname(recipientInfo.getSurname());
        senderEvent.setCardNumberRecipient(recipient);
        senderEvent.setAmount("- " + sumToCharge);
        senderEvent.setClientsLogin(senderAccount.getClientsLogin());
        senderEvent.setSenderSurname(senderInfo.getSurname());
        senderEvent.setSenderName(senderInfo.getName());
        senderEvent.setRates(rate);

        ClientsHistory recipientEvent = new ClientsHistory();
        recipientEvent.setData(dataNow);
        recipientEvent.setCardNumberSender(sender);
        recipientEvent.setRecipientName(recipientInfo.getName());
        recipientEvent.setRecipientSurname(recipientInfo.getSurname());
        recipientEvent.setCardNumberRecipient(recipient);
        recipientEvent.setAmount("+ " + sumToSend);
        recipientEvent.setClientsLogin(recipientAccount.getClientsLogin());
        recipientEvent.setSenderSurname(senderInfo.getSurname());
        recipientEvent.setSenderName(senderInfo.getName());
        recipientEvent.setRates(rate);

        clientsHistoryRepository.saveAll(Arrays.asList(senderEvent, recipientEvent));
        return "Done! Payment sent.";
    }

    public String exchangeOperation(Long fromCard, Double sum, Long toCard){
        Long accountId = clientsAccountRepository.findIdsByColumnsContaining(fromCard);
        ClientsAccount account = accountService.getClientsAccountById(accountId);
        ClientsInformation info = accountService.getClientsInformationById(accountId);

        Currency fromCurrency = getCardCurrency(account, fromCard);
        Currency toCurrency = getCardCurrency(account, toCard);

        Double rate = getRate(fromCurrency, toCurrency);
        System.out.println("new transfer " + fromCurrency + " - " + toCurrency);

        double newFromValue = round(account.getValue(fromCurrency) - sum);
        account.setValueToCard(fromCurrency, newFromValue);

        double newToValue = round(account.getValue(fromCurrency) + (sum * rate));
        account.setValueToCard(toCurrency, newToValue);

        String dataNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ClientsHistory historyEntry = new ClientsHistory();
        historyEntry.setData(dataNow);
        historyEntry.setCardNumberSender(account.getCardEur());
        historyEntry.setRecipientName(info.getName());
        historyEntry.setRecipientSurname(info.getSurname());
        historyEntry.setCardNumberRecipient(account.getCardUah());
        historyEntry.setAmount("~ " + sum);
        historyEntry.setClientsLogin(account.getClientsLogin());
        historyEntry.setSenderSurname(" ");
        historyEntry.setSenderName("exchange eur->uah");
        historyEntry.setRates(rate);

        clientsHistoryRepository.save(historyEntry);
        return "Done! Payment sent.";
    }

    private Double getRate(Currency from, Currency to) {
        if (from == to) {
            return 1.0;
        }
        if (from == EUR && to == UAH) {
            return ratesRepository.getReferenceById(1L).getExchangeRate();
        }
        if (from == EUR && to == USD) {
            return ratesRepository.getReferenceById(2L).getExchangeRate();
        }
        if (from == USD && to == UAH) {
            return ratesRepository.getReferenceById(3L).getExchangeRate();
        }
        if (from == USD && to == EUR) {
            return ratesRepository.getReferenceById(4L).getExchangeRate();
        }
        if (from == UAH && to == USD) {
            return ratesRepository.getReferenceById(5L).getExchangeRate();
        }
        if (from == UAH && to == EUR) {
            return ratesRepository.getReferenceById(6L).getExchangeRate();
        }
        throw new UnsupportedOperationException("Cannot convert " + from + " to " + to);
    }

}
