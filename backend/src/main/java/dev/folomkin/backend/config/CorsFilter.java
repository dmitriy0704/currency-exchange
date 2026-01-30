package dev.folomkin.backend.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "*");  // Или конкретный origin, например "http://localhost:3000"
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");

        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Если нужны куки/авторизация
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
    }
}