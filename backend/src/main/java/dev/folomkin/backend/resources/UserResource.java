// src/main/java/dev/folomkin/backend/resources/UserResource.java
package dev.folomkin.backend.resources;

import dev.folomkin.backend.exception.ConflictException;
import dev.folomkin.backend.repository.UserRepository;
import dev.folomkin.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import dev.folomkin.backend.model.User;

import java.sql.*;

@Path("/users")
public class UserResource {

    @Context
    private ServletContext context;
    private UserService userService;

    @PostConstruct
    public void init() {
        UserRepository repository = new UserRepository(context);
        this.userService = new UserService(repository);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@FormParam("name") String name,
                               @FormParam("email") String email) {
        try {
            User user = userService.createUser(name, email);
            return Response.status(201).entity(user).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (ConflictException e) {
            return Response.status(409).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"error\": \"Ошибка базы данных\"}").build();
        }
    }

    @GET
    public Response getAllUsers() {
        try {
            return Response.ok(userService.getAllUsers()).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/{email}")
    public Response getUserByEmail(@PathParam("email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return Response.status(404).entity("{\"error\": \"Пользователь не найден\"}").build();
            }
            return Response.ok(user).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }
}