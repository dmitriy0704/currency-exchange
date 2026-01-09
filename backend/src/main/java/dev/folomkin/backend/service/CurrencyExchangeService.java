package dev.folomkin.backend.service;

import dev.folomkin.backend.model.ConversionResultDto;
import dev.folomkin.backend.repository.CurrencyExchangeRepository;

import java.math.BigDecimal;

public class CurrencyExchangeService {

    private final CurrencyExchangeRepository repository;

    public CurrencyExchangeService(CurrencyExchangeRepository repository) {
        this.repository = repository;
    }

    public ConversionResultDto calcCurrencyExchange(
            String fromCode, String toCode, BigDecimal amount) {
        return repository.calcCurrencyExchange(fromCode, toCode, amount);
    }
}
