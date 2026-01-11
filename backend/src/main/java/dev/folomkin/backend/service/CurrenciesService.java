package dev.folomkin.backend.service;

import dev.folomkin.backend.exception.AlreadyExistsException;
import dev.folomkin.backend.exception.ConflictException;
import dev.folomkin.backend.exception.NotFoundException;
import dev.folomkin.backend.model.CreateCurrencyRequestDto;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.repository.CurrenciesRepository;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
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


    public Currency createCurrency(CreateCurrencyRequestDto requestDto) throws SQLException {
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Национальность валюты обязательна");
        }
        if (requestDto.getCode() == null || requestDto.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Код валюты обязателен");
        }

        String normalizedCode = requestDto.getCode().trim().toUpperCase();
        String normalizedName = requestDto.getName().trim();
        String normalizedSign = requestDto.getSign().trim();
        BigDecimal rubRate = requestDto.getRub_rate();

        try {
            // Пытаемся найти валюту по коду
            currenciesRepository.findByCode(normalizedCode);

            // Если метод не бросил исключение — валюта уже существует
            throw new AlreadyExistsException("Валюта с кодом " + normalizedCode + " уже существует");

        } catch (NotFoundException e) {
            // Валюты нет — можно создавать новую
            return currenciesRepository.save(normalizedName, normalizedCode, rubRate, normalizedSign);
        }
    }
}
