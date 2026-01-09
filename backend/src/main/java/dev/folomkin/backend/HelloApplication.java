package dev.folomkin.backend;

import dev.folomkin.backend.config.CorsFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class HelloApplication extends Application {
//    @Override
//    public Set<Class<?>> getClasses() {
//        Set<Class<?>> classes = new HashSet<>();
//        classes.add(CorsFilter.class);  // Явно добавьте, если не работает автоматически
//        // Добавьте ваши ресурсы: classes.add(UserResource.class); и т.д.
//        return classes;
//    }
}
