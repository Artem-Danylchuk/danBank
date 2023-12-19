package Bank.backBank.utils;

import Bank.backBank.entity.ClientsAccount;
import Bank.backBank.entity.Currency;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static Bank.backBank.entity.Currency.*;

@UtilityClass
public class CurrencyUtils {

    public static Currency getCardCurrency(ClientsAccount account, Long cardNumber) {
        if (account.getCardEur().equals(cardNumber)) {
            return EUR;
        }
        if (account.getCardUah().equals(cardNumber)) {
            return UAH;
        }
        if (account.getCardUsd().equals(cardNumber)) {
            return USD;
        }
        throw new RuntimeException("Account " + account.getId() + " doesn't contain such card " + cardNumber);
    }

    public static double round(double number) {
        return new BigDecimal(number)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

}
