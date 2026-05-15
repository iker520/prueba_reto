package com.example.mourosub.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Interceptor que inyecta la URI actual en el modelo de todas las vistas.
 * Necesario porque Thymeleaf 3.1+ (Spring Boot 3) no permite acceder a
 * #httpServletRequest directamente por razones de seguridad.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

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
