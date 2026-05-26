package com.example.mourosub.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración MVC:
 * - Interceptor que inyecta la URI actual en el modelo (necesario para Thymeleaf 3.1+)
 * - Mapeo de la carpeta de uploads locales como recurso estático accesible vía /uploads/**
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.path}")
    private String uploadsPath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Sirve los ficheros subidos desde la carpeta local como si fueran estáticos
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsPath + "/");
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull Object handler,
                                   ModelAndView modelAndView) {
                if (modelAndView != null) {
                    modelAndView.addObject("currentUri", request.getRequestURI());
                }
            }
        });
    }
}
