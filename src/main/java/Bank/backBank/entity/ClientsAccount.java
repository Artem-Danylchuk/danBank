package Bank.backBank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="clients_account")
@Getter
@Setter
@NoArgsConstructor
public class ClientsAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(name = "UAH",nullable = false,columnDefinition = "Double default 0")
    private Double uah;
    @Column(name = "cardUah")
    private Long cardUah;
    @Column(name = "cvvCardUah")
    private Long cvvCardUah;
    @Column(name = "currencyUah")
    private LocalDate currencyUah;

    @Column(name = "EUR",nullable = false,columnDefinition = "Double default 0")
    private Double eur;
    @Column(name = "cardEur")
    private Long cardEur;
    @Column(name = "cvvCardEur")
    private Long cvvCardEur;
    @Column(name = "currencyEur")
    private LocalDate currencyEur;

    @Column(name = "USD",nullable = false,columnDefinition = "Double default 0")
    private Double usd;
    @Column(name = "cardUsd")
    private Long cardUsd;
    @Column(name = "cvvCardUsd")
    private Long cvvCardUsd;
    @Column(name = "currencyUsd")
    private LocalDate currencyUsd;

    @OneToOne(mappedBy = "clientsAccount")
    private ClientsLogin clientsLogin;

    public ClientsAccount(Double uah, Long cardUah, Long cvvCardUah, LocalDate currencyUah, Double eur, Long cardEur, Long cvvCardEur, LocalDate currencyEur, Double usd, Long cardUsd, Long cvvCardUsd, LocalDate currencyUsd) {
        this.uah = uah;
        this.cardUah = cardUah;
        this.cvvCardUah = cvvCardUah;
        this.currencyUah = currencyUah;
        this.eur = eur;
        this.cardEur = cardEur;
        this.cvvCardEur = cvvCardEur;
        this.currencyEur = currencyEur;
        this.usd = usd;
        this.cardUsd = cardUsd;
        this.cvvCardUsd = cvvCardUsd;
        this.currencyUsd = currencyUsd;
    }

    public void setValueToCard(Currency currency, double value) {
        switch (currency) {
            case EUR -> setEur(value);
            case UAH -> setUah(value);
            case USD -> setUsd(value);
            default -> throw new UnsupportedOperationException("Unsupported currency " + currency);
        }
    }

    public double getValue(Currency currency) {
        if (currency == Currency.UAH) {
            return getUah();
        }
        if (currency == Currency.USD) {
            return getUsd();
        }
        if (currency == Currency.EUR) {
            return getEur();
        }
        throw new UnsupportedOperationException("Unsupported currency " + currency);
    }

}
