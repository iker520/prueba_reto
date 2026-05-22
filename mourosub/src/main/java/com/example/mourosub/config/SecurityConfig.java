package com.example.mourosub.config;

import com.example.mourosub.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public DaoAuthenticationProvider authenticationProvider(UsuarioService usuarioService, BCryptPasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                                               UsuarioService usuarioService,
                                               BCryptPasswordEncoder passwordEncoder,
                                               CustomLoginSuccessHandler loginSuccessHandler) throws Exception {

                CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                requestHandler.setCsrfRequestAttributeName(null);

                http
                                .authenticationProvider(authenticationProvider(usuarioService, passwordEncoder))
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                .csrfTokenRequestHandler(requestHandler))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/", "/servicios/**", "/conocenos",
                                                                "/noticias/**", "/contacto", "/login",
                                                                "/registro", "/registro/**",
                                                                "/css/**", "/js/**", "/images/**",
                                                                "/uploads/**", "/uploads/seguros/**",
                                                                "/webjars/**", "/favicon.ico",
                                                                "/error/**", "/403",
                                                                "/condiciones-venta", "/devoluciones",
                                                                "/aviso-legal", "/politica-privacidad",
                                                                "/newsletter/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .successHandler(loginSuccessHandler)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/403"));

                return http.build();
        }
}
