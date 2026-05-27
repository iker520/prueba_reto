package com.example.mourosub.service;

import com.example.mourosub.model.Usuario;
import com.example.mourosub.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
/*Usuario*/
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setDniUsuario("12345678A");
        usuario.setEmail("test@mourosub.com");
        usuario.setPassword("$2a$10$hashedPassword");
        usuario.setNombre("Juan");
        usuario.setApellido1("García");
        usuario.setApellido2("Juan");
        usuario.setRol("ROLE_USER");
        usuario.setEsBuceador(false);
        usuario.setEstadoSeguro("PENDIENTE");
    }

    // ─── loadUserByUsername ───────────────────────────────────
    @Test
    @DisplayName("loadUserByUsername carga usuario existente correctamente")
    void loadUserByUsername_usuarioExistente() {
        when(usuarioRepository.findByEmail("test@mourosub.com")).thenReturn(Optional.of(usuario));

        UserDetails result = usuarioService.loadUserByUsername("test@mourosub.com");

        assertThat(result.getUsername()).isEqualTo("test@mourosub.com");
        assertThat(result.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("loadUserByUsername lanza excepción si no existe el usuario")
    void loadUserByUsername_usuarioNoExistente_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("noexiste@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.loadUserByUsername("noexiste@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("noexiste@email.com");
    }

    // ─── findAll ──────────────────────────────────────────────
    @Test
    @DisplayName("findAll retorna todos los usuarios")
    void findAll_retornaTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@mourosub.com");
    }

    // ─── findById ─────────────────────────────────────────────
    @Test
    @DisplayName("findById retorna usuario si existe")
    void findById_retornaUsuarioSiExiste() {
        when(usuarioRepository.findById("12345678A")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findById("12345678A");

        assertThat(result).isPresent();
        assertThat(result.get().getDniUsuario()).isEqualTo("12345678A");
    }

    @Test
    @DisplayName("findById retorna vacío si no existe")
    void findById_retornaVacioSiNoExiste() {
        when(usuarioRepository.findById("00000000X")).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.findById("00000000X");

        assertThat(result).isEmpty();
    }

    // ─── create ───────────────────────────────────────────────
    @Test
    @DisplayName("create encripta la contraseña y guarda el usuario")
    void create_encriptaPasswordYGuarda() {
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashed");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.create(usuario, "password123");

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(usuario);
    }

    // ─── save ─────────────────────────────────────────────────
    @Test
    @DisplayName("save establece fechaRegistro si es null")
    void save_estableceFechaRegistroSiEsNull() {
        usuario.setFechaRegistro(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.save(usuario);

        assertThat(usuario.getFechaRegistro()).isNotNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("save no sobreescribe password si llega vacío")
    void save_noSobreescribePasswordVacio() {
        usuario.setPassword("");
        Usuario existente = new Usuario();
        existente.setDniUsuario("12345678A");
        existente.setPassword("$2a$10$passwordAnterior");

        when(usuarioRepository.findById("12345678A")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.save(usuario);

        assertThat(usuario.getPassword()).isEqualTo("$2a$10$passwordAnterior");
    }

    // ─── updatePassword ───────────────────────────────────────
    @Test
    @DisplayName("updatePassword encripta y actualiza la contraseña")
    void updatePassword_encriptaYActualiza() {
        when(usuarioRepository.findById("12345678A")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nuevaPassword")).thenReturn("$2a$10$newHash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.updatePassword("12345678A", "nuevaPassword");

        verify(passwordEncoder).encode("nuevaPassword");
        assertThat(usuario.getPassword()).isEqualTo("$2a$10$newHash");
    }

    // ─── deleteById ───────────────────────────────────────────
    @Test
    @DisplayName("deleteById llama al repositorio correctamente")
    void deleteById_llamaAlRepositorio() {
        doNothing().when(usuarioRepository).deleteById("12345678A");

        usuarioService.deleteById("12345678A");

        verify(usuarioRepository).deleteById("12345678A");
    }

    // ─── existsByEmail ────────────────────────────────────────
    @Test
    @DisplayName("existsByEmail retorna true si el email existe")
    void existsByEmail_retornaTrueSiExiste() {
        when(usuarioRepository.existsByEmail("test@mourosub.com")).thenReturn(true);

        boolean result = usuarioService.existsByEmail("test@mourosub.com");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByEmail retorna false si el email no existe")
    void existsByEmail_retornaFalseSiNoExiste() {
        when(usuarioRepository.existsByEmail("nuevo@email.com")).thenReturn(false);

        boolean result = usuarioService.existsByEmail("nuevo@email.com");

        assertThat(result).isFalse();
    }

    // ─── count ────────────────────────────────────────────────
    @Test
    @DisplayName("count retorna el número de usuarios")
    void count_retornaTotalUsuarios() {
        when(usuarioRepository.count()).thenReturn(10L);

        long result = usuarioService.count();

        assertThat(result).isEqualTo(10L);
    }

    // ─── aprobarSeguro / rechazarSeguro ───────────────────────
    @Test
    @DisplayName("aprobarSeguro cambia estado a APROBADO")
    void aprobarSeguro_cambiaEstadoAAprobado() {
        usuario.setEsBuceador(true);
        usuario.setEstadoSeguro("PENDIENTE");
        when(usuarioRepository.findById("12345678A")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.aprobarSeguro("12345678A");

        assertThat(usuario.getEstadoSeguro()).isEqualTo("APROBADO");
    }

    @Test
    @DisplayName("rechazarSeguro cambia estado a RECHAZADO")
    void rechazarSeguro_cambiaEstadoARechazado() {
        usuario.setEsBuceador(true);
        usuario.setEstadoSeguro("PENDIENTE");
        when(usuarioRepository.findById("12345678A")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.rechazarSeguro("12345678A");

        assertThat(usuario.getEstadoSeguro()).isEqualTo("RECHAZADO");
    }

    // ─── countBuceadoresSeguroPendiente ───────────────────────
    @Test
    @DisplayName("countBuceadoresSeguroPendiente retorna el número correcto")
    void countBuceadoresSeguroPendiente_retornaConteo() {
        when(usuarioRepository.countByEsBuceadorTrueAndEstadoSeguro("PENDIENTE")).thenReturn(3L);

        long result = usuarioService.countBuceadoresSeguroPendiente();

        assertThat(result).isEqualTo(3L);
    }
}