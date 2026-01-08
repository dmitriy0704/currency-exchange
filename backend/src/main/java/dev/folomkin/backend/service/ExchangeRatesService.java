package dev.folomkin.backend.service;

import dev.folomkin.backend.model.ExchangeRate;
import dev.folomkin.backend.repository.ExchangeRatesRepository;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRatesService {

    private final ExchangeRatesRepository repository;


    public ExchangeRatesService(ExchangeRatesRepository repository) {
        this.repository = repository;
    }

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return repository.findAll();
    }
}
