package dev.folomkin.backend.resources.exchange_rates;

import dev.folomkin.backend.model.CreateExchangeRatesRequestDto;
import dev.folomkin.backend.model.ExchangeRate;
import dev.folomkin.backend.repository.ExchangeRatesRepository;
import dev.folomkin.backend.service.ExchangeRatesService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/exchangeRates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class ExchangeRatesResource {

    @Context
    private ServletContext context;

    private ExchangeRatesService service;


    @PostConstruct
    public void init() {
        ExchangeRatesRepository repository = new ExchangeRatesRepository(context);
        this.service = new ExchangeRatesService(repository);
    }

    /**
     * GET /exchangeRates
     * Получение списка всех обменных курсов
     */
    @GET
    public Response getAllExchangeRates() {
        try {
            return Response.ok(service.getAllExchangeRates()).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }

    /**
     * POST /exchangeRates
     * Тело запроса: x-www-form-urlencoded
     * Поля: baseCurrencyCode=id базовой валюты;
     *       targetCurrencyCode=id конечной валюты;
     * Создает обменный курс
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addExchangeRate(
            @FormParam("baseCurrencyCode") String baseCode,
            @FormParam("targetCurrencyCode") String targetCode
//            @FormParam("rate") BigDecimal rate
    ) {
        try {

            CreateExchangeRatesRequestDto ratesDto = new CreateExchangeRatesRequestDto(baseCode, targetCode);
            ExchangeRate exchangeRate = service.createExchangeRates(ratesDto);
            return Response.status(201).entity(exchangeRate).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }
}
