package org.ceylonsmunich.service.controllers;

import org.ceylonsmunich.service.entity.Currency;
import org.ceylonsmunich.service.entity.repos.CurrencyRepository;
import org.ceylonsmunich.service.entity.ExchangeRate;
import org.ceylonsmunich.service.entity.repos.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @RequestMapping(path = "api/currency", method = RequestMethod.GET)
    public List<Currency> getAllCurrency(){
        return currencyRepository.findAll();
    }

    @RequestMapping(path = "api/currency/add", method = RequestMethod.POST)
    public void addCurrency(@RequestBody List<Currency> currency){
        currencyRepository.saveAll(currency);
    }

    @RequestMapping(path = "api/currency/remove", method = RequestMethod.DELETE)
    public void deleteCurrency(@RequestBody Currency currency){
        currencyRepository.delete(currency);
    }

    @RequestMapping(path = "api/exchangerate", method = RequestMethod.GET)
    public List<ExchangeRate> getAllExchangeRates(){
        return exchangeRateRepository.findAll();
    }
    @RequestMapping(path = "api/exchangerate/update", method = RequestMethod.POST)
    public void updateExchangeRate(@RequestBody ExchangeRate rate){
        exchangeRateRepository.save(rate);
    }
    @RequestMapping(path = "api/exchangerate/batchupdate", method = RequestMethod.POST)
    public void batchUpdateExchangeRate(@RequestBody List<ExchangeRate> rate){
        exchangeRateRepository.saveAll(rate);
    }


    @RequestMapping(path = "api/exchangerate/remove", method = RequestMethod.DELETE)
    public void deleteExchangeRate(@RequestBody ExchangeRate rate){
        exchangeRateRepository.save(rate);
    }

}
