package com.example.mourosub.service;

import com.example.mourosub.model.Usuario;
import com.example.mourosub.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(usuario.getRol().replace("ROLE_", ""))
            .build();
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(String dni) {
        return usuarioRepository.findById(dni);
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDate.now().atStartOfDay());
        }
        // Si el password llega vacío (edición sin cambio de contraseña),
        // recuperamos el hash actual de la BD para no sobreescribirlo con null
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            usuarioRepository.findById(usuario.getDniUsuario())
                    .ifPresent(existing -> usuario.setPassword(existing.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario create(Usuario usuario, String rawPassword) {
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuario.setFechaRegistro(LocalDate.now().atStartOfDay());
        return usuarioRepository.save(usuario);
    }

    public void updatePassword(String dni, String rawPassword) {
        usuarioRepository.findById(dni).ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(rawPassword));
            usuarioRepository.save(u);
        });
    }

    public void deleteById(String dni) {
        usuarioRepository.deleteById(dni);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public long count() {
        return usuarioRepository.count();
    }

    /** Número de buceadores con seguro pendiente de validación (para campanita). */
    public long countBuceadoresSeguroPendiente() {
        return usuarioRepository.countByEsBuceadorTrueAndEstadoSeguro("PENDIENTE");
    }

    /** Lista de buceadores con seguro pendiente. */
    public java.util.List<Usuario> findBuceadoresSeguroPendiente() {
        return usuarioRepository.findByEsBuceadorTrueAndEstadoSeguro("PENDIENTE");
    }

    /** Aprueba el seguro de un buceador. */
    public void aprobarSeguro(String dni) {
        usuarioRepository.findById(dni).ifPresent(u -> {
            u.setEstadoSeguro("APROBADO");
            usuarioRepository.save(u);
        });
    }

    /** Rechaza el seguro de un buceador. */
    public void rechazarSeguro(String dni) {
        usuarioRepository.findById(dni).ifPresent(u -> {
            u.setEstadoSeguro("RECHAZADO");
            usuarioRepository.save(u);
        });
    }
}
