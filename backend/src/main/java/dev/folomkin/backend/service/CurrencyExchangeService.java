package dev.folomkin.backend.service;

import dev.folomkin.backend.model.ConversionResultDto;
import dev.folomkin.backend.repository.ExchangeRatesRepository;

import java.math.BigDecimal;

public class CurrencyExchangeService {

    private final ExchangeRatesRepository repository;

    public CurrencyExchangeService(ExchangeRatesRepository repository) {
        this.repository = repository;
    }

    public ConversionResultDto convertCurrency(String fromCode, String toCode, BigDecimal amount) {
        return repository.convertCurrency(fromCode, toCode, amount);
    }
}
