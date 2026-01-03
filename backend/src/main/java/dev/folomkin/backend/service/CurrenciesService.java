package dev.folomkin.backend.service;

import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.repository.CurrenciesRepository;

import java.sql.SQLException;
import java.util.List;

public class CurrenciesService {

    private final CurrenciesRepository currenciesRepository;

    public CurrenciesService(CurrenciesRepository currenciesRepository) {
        this.currenciesRepository = currenciesRepository;
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        return currenciesRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) throws SQLException {
        return currenciesRepository.findByCode(code);
    }
}
