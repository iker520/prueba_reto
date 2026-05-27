package com.example.mourosub.service;

import com.example.mourosub.model.Contacto;
import com.example.mourosub.repository.ContactoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;

    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    public Contacto guardar(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    public List<Contacto> findAll() {
        return contactoRepository.findAllByOrderByFechaEnvioDesc();
    }

    public List<Contacto> findByEstado(String estado) {
        return contactoRepository.findByEstadoOrderByFechaEnvioDesc(estado);
    }

    public Optional<Contacto> findById(Long id) {
        return contactoRepository.findById(id);
    }

    public void cambiarEstado(Long id, String nuevoEstado) {
        contactoRepository.findById(id).ifPresent(c -> {
            c.setEstado(nuevoEstado);
            contactoRepository.save(c);
        });
    }

    public void deleteById(Long id) {
        contactoRepository.deleteById(id);
    }

    public long countNuevas() {
        return contactoRepository.countByEstado("NUEVA");
    }

    public static List<String> getEstadosDisponibles() {
        return List.of("NUEVA", "LEIDA", "RESPONDIDA");
    }
}
