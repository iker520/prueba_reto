package com.example.mourosub.service;

import com.example.mourosub.model.Certificacion;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.repository.CertificacionRepository;
import com.example.mourosub.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CertificacionService {

    private final CertificacionRepository certificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public CertificacionService(CertificacionRepository certificacionRepository,
                                UsuarioRepository usuarioRepository) {
        this.certificacionRepository = certificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- Consultas generales ---

    public List<Certificacion> findAll() {
        return certificacionRepository.findAll();
    }

    public Optional<Certificacion> findById(Long id) {
        return certificacionRepository.findById(id);
    }

    public List<Certificacion> findByUsuario(String dniUsuario) {
        return certificacionRepository.findByUsuarioDniUsuario(dniUsuario);
    }

    public List<Certificacion> findPropiasByUsuario(String dniUsuario) {
        return certificacionRepository.findByUsuarioDniUsuarioAndExpedidaPorMourosubFalse(dniUsuario);
    }

    public List<Certificacion> findMouroSubByUsuario(String dniUsuario) {
        return certificacionRepository.findByUsuarioDniUsuarioAndExpedidaPorMourosubTrue(dniUsuario);
    }

    public List<Certificacion> findAllMouroSub() {
        return certificacionRepository.findByExpedidaPorMourosubTrue();
    }

    /** Certificaciones de usuario pendientes de revisión por el admin. */
    public List<Certificacion> findPendientesRevision() {
        return certificacionRepository.findByRevisadaFalseAndExpedidaPorMourosubFalse();
    }

    /** Número de certificaciones pendientes de revisión (para el badge de campanita). */
    public long countPendientesRevision() {
        return certificacionRepository.countByRevisadaFalseAndExpedidaPorMourosubFalse();
    }

    // --- Operaciones CRUD ---

    public Certificacion save(Certificacion cert) {
        if (cert.getFechaRegistro() == null) {
            cert.setFechaRegistro(LocalDateTime.now());
        }
        return certificacionRepository.save(cert);
    }

    public void deleteById(Long id) {
        certificacionRepository.deleteById(id);
    }

    // --- Acciones de validación (Admin) ---

    public boolean revisar(Long id, boolean validar, String notasAdmin) {
        return certificacionRepository.findById(id).map(cert -> {
            cert.setRevisada(true);
            cert.setValidada(validar);
            cert.setNotasAdmin(notasAdmin);
            certificacionRepository.save(cert);
            return true;
        }).orElse(false);
    }

    // --- Alta de certificación por MouroSub (desde Admin) ---

    public Certificacion crearCertificacionMouro(String dniUsuario, Certificacion cert) {
        Usuario usuario = usuarioRepository.findById(dniUsuario)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Usuario no encontrado: " + dniUsuario));
        cert.setUsuario(usuario);
        cert.setExpedidaPorMourosub(true);
        cert.setRevisada(true);   // ya revisada (la emite Mouro)
        cert.setValidada(true);   // ya validada
        cert.setFechaRegistro(LocalDateTime.now());
        return certificacionRepository.save(cert);
    }
}
