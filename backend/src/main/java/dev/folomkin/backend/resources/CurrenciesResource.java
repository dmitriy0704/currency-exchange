package dev.folomkin.backend.resources;

import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.repository.UserRepository;
import dev.folomkin.backend.service.CurrencyService;
import dev.folomkin.backend.service.UserService;
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

    private CurrencyService currencyService;


    @PostConstruct
    public void init() {
        CurrenciesRepository repository = new CurrenciesRepository(context);
        this.currencyService = new CurrencyService(repository);
    }


    @GET
    public Response getAllCurrencies() {
        try {
            return Response.ok(currencyService.getAllCurrencies()).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }
}
