package dev.folomkin.backend.resources;

import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.repository.ExchangeRatesRepository;
import dev.folomkin.backend.service.CurrenciesService;
import dev.folomkin.backend.service.ExchangeRatesService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

}
