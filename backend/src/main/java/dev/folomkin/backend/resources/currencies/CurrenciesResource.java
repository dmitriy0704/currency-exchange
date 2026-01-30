package dev.folomkin.backend.resources.currencies;

import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.service.CurrenciesService;
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

@Path("/currencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class CurrenciesResource {

    @Context
    private ServletContext context;

    private CurrenciesService currenciesService;


    @PostConstruct
    public void init() {
        CurrenciesRepository repository = new CurrenciesRepository(context);
        this.currenciesService = new CurrenciesService(repository);
    }

    @GET
    public Response getAllCurrencies() {
        try {
            return Response.ok(currenciesService.getAllCurrencies()).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }
}
