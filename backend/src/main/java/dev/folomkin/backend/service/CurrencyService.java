package dev.folomkin.backend.service;

import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.User;
import dev.folomkin.backend.repository.CurrenciesRepository;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {

    private final CurrenciesRepository currenciesRepository;

    public CurrencyService(CurrenciesRepository currenciesRepository) {
        this.currenciesRepository = currenciesRepository;
    }



    public List<Currency> getAllCurrencies() throws SQLException {
        return currenciesRepository.findAll();
    }
}
