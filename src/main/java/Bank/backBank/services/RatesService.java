package Bank.backBank.services;

import Bank.backBank.entity.Rates;
import Bank.backBank.json.Rate;
import Bank.backBank.repository.RatesRepository;
import Bank.backBank.utils.CurrencyUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class RatesService {

    private static final String URL_EUR = "https://open.er-api.com/v6/latest/EUR?apikey=5dc2b251df8d4a8cad28e6f6106e0e96";
    private static final String URL_USD = "https://open.er-api.com/v6/latest/USD?apikey=5dc2b251df8d4a8cad28e6f6106e0e96";
    private static final String URL_UAH = "https://open.er-api.com/v6/latest/UAH?apikey=5dc2b251df8d4a8cad28e6f6106e0e96";

    private final RatesRepository rateRepository;
    private final RatesRepository ratesRepository;

    public RatesService(RatesRepository rateRepository, RatesRepository ratesRepository) {
        this.rateRepository = rateRepository;
        this.ratesRepository = ratesRepository;
    }

    @PostConstruct
    public void init() {
        updateRate();
    }
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000, zone = "GMT")
    public void updateRate() {

        Rate rateUER = getRateEUR();
        Optional<Rates> existingRatesEURtoUAH = ratesRepository.findByCurrency("EURtoUAH");
        existingRatesEURtoUAH.ifPresent(rates -> {
            rates.setExchangeRate(rateUER.getRates().getUah());
      //      ratesRepository.save(rates);
        });
        Optional<Rates> existingRatesEURtoUSD = ratesRepository.findByCurrency("EURtoUSD");
        existingRatesEURtoUSD.ifPresent(rates -> {
            rates.setExchangeRate(rateUER.getRates().getUsd());
      //      ratesRepository.save(rates);
        });
        existingRatesEURtoUAH.orElseGet(() -> ratesRepository.save(new Rates("EURtoUAH", CurrencyUtils.round(rateUER.getRates().getUah()))));
        existingRatesEURtoUSD.orElseGet(() -> ratesRepository.save(new Rates("EURtoUSD", CurrencyUtils.round(rateUER.getRates().getUsd()))));


        Rate rateUSD = getRateUSD();
        Optional<Rates> existingRatesUSDtoUAH = ratesRepository.findByCurrency("USDtoUAH");
        existingRatesUSDtoUAH.ifPresent(rates -> {
            rates.setExchangeRate(rateUSD.getRates().getUah());
        });
        Optional<Rates> existingRatesUSDtoEUR = ratesRepository.findByCurrency("USDtoEUR");
        existingRatesUSDtoEUR.ifPresent(rates -> {
            rates.setExchangeRate(rateUSD.getRates().getEur());
        });
        existingRatesUSDtoUAH.orElseGet(() -> ratesRepository.save(new Rates("USDtoUAH",CurrencyUtils.round(rateUSD.getRates().getUah()))));
        existingRatesUSDtoEUR.orElseGet(() -> ratesRepository.save(new Rates("USDtoEUR",CurrencyUtils.round(rateUSD.getRates().getEur()))));

        Rate rateUAH = getRateUAH();
        Optional<Rates> existingRatesUAHtoUSD = ratesRepository.findByCurrency("UAHtoUSD");
        existingRatesUAHtoUSD.ifPresent(rates -> {
            rates.setExchangeRate(rateUAH.getRates().getUsd());
        });
        Optional<Rates> existingRatesUAHtoEUR = ratesRepository.findByCurrency("UAHtoEUR");
        existingRatesUAHtoEUR.ifPresent(rates -> {
            rates.setExchangeRate(rateUAH.getRates().getEur());
        });
        existingRatesUAHtoUSD.orElseGet(() -> ratesRepository.save(new Rates("UAHtoUSD",CurrencyUtils.round(rateUAH.getRates().getUsd()))));
        existingRatesUAHtoEUR.orElseGet(()->ratesRepository.save(new Rates("UAHtoEUR",CurrencyUtils.round(rateUAH.getRates().getEur()))));

    }
//    private Double getSmallNumberFromJson(Double ratesFromJson){
//        BigDecimal roundedRate = BigDecimal.valueOf(ratesFromJson).setScale(2, RoundingMode.HALF_UP);
//        Double rates = roundedRate.doubleValue();
//        return  rates;
//    }
    public Rate getRateEUR() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Rate> response = restTemplate.getForEntity(URL_EUR, Rate.class);
            return response.getBody();
        } catch (Exception ex) {
            return new Rate();
        }
    }
    public Rate getRateUAH() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Rate> response = restTemplate.getForEntity(URL_UAH, Rate.class);
            return response.getBody();
        } catch (Exception ex) {
            return new Rate();
        }
    }
    public Rate getRateUSD() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Rate> response = restTemplate.getForEntity(URL_USD, Rate.class);
            return response.getBody();
        } catch (Exception ex) {
            return new Rate();
        }
    }

}
