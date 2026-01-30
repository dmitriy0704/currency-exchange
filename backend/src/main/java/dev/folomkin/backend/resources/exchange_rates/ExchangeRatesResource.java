package dev.folomkin.backend.resources.exchange_rates;

import dev.folomkin.backend.model.CreateExchangeRatesDto;
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


    @GET
    public Response getAllExchangeRates() {
        try {
            return Response.ok(service.getAllExchangeRates()).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addExchangeRate(
            @FormParam("baseCurrencyCode") String baseCode,
            @FormParam("targetCurrencyCode") String targetCode
//            @FormParam("rate") BigDecimal rate
    ) {
        try {

            CreateExchangeRatesDto  ratesDto = new CreateExchangeRatesDto(baseCode, targetCode);

            ExchangeRate exchangeRate = service.createExchangeRates(ratesDto);
            return Response.status(201).entity(exchangeRate).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }
}
