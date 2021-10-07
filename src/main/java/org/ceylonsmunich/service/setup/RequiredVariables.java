package org.ceylonsmunich.service.setup;

import org.ceylonsmunich.service.entity.Currency;
import org.ceylonsmunich.service.entity.Parameters;
import org.ceylonsmunich.service.entity.repos.CurrencyRepository;
import org.ceylonsmunich.service.entity.ExchangeRate;
import org.ceylonsmunich.service.entity.repos.ExchangeRateRepository;
import org.ceylonsmunich.service.entity.repos.ParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RequiredVariables implements CommandLineRunner {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Override
    public void run(String... args) throws Exception {
        currencyRepository.save(new Currency("LKR","LKR",false));
        currencyRepository.save(new Currency("USD","$",true));
        currencyRepository.save(new Currency("EUR","€",true));

        exchangeRateRepository.save(new ExchangeRate("LKR","EUR",1/206.5));
        exchangeRateRepository.save(new ExchangeRate("LKR","USD",1/189.89));
        exchangeRateRepository.save(new ExchangeRate("USD","EUR",1.09));

        parameterRepository.save(new Parameters("BASE_CURRENCY","EUR","System wide base currency"));
        parameterRepository.save(new Parameters("DEFAULT_CONSIGNMENT_PERIOD","30","Default consignment period in days"));
    }
}
