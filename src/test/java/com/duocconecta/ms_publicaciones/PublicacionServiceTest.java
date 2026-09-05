package com.duocconecta.ms_publicaciones;

import com.duocconecta.ms_publicaciones.domain.Publicacion;
import com.duocconecta.ms_publicaciones.domain.TipoRecurso;
import com.duocconecta.ms_publicaciones.dto.PublicacionRequestDTO;
import com.duocconecta.ms_publicaciones.exception.OperacionNoPermitidaException;
import com.duocconecta.ms_publicaciones.exception.RecursoNoEncontradoException;
import com.duocconecta.ms_publicaciones.repository.PublicacionRepository;
import com.duocconecta.ms_publicaciones.service.PublicacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la capa de servicio, sin levantar contexto de Spring
 * (evita depender de la conexión al IDaaS o a la base de datos real).
 */
@ExtendWith(MockitoExtension.class)
class PublicacionServiceTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private PublicacionService publicacionService;

    private final String autorId = "usuario-oid-123";

    @BeforeEach
    void setUp() {
        // Cada test parte limpio; Mockito reinicia los mocks automáticamente por defecto.
    }

    @Test
void crear_deberiaGuardarPublicacionConAutorDelToken() {
    PublicacionRequestDTO dto = new PublicacionRequestDTO(
            "Guía de Spring Security", "Cómo configurar OAuth2 resource server",
            "Ingeniería en Informática", "Sede Concepción", TipoRecurso.GUIA_MATERIA,
            List.of("spring", "seguridad"), List.of(), null);

    when(publicacionRepository.save(any(Publicacion.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Publicacion resultado = publicacionService.crear(dto, autorId);

    assertThat(resultado.getAutorId()).isEqualTo(autorId);
    assertThat(resultado.getTitulo()).isEqualTo("Guía de Spring Security");
    verify(publicacionRepository, times(1)).save(any(Publicacion.class));
}

    @Test
    void obtenerPorId_deberiaLanzarExcepcionSiNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(publicacionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicacionService.obtenerPorId(idInexistente))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminar_deberiaRechazarSiElUsuarioNoEsElAutor() {
        UUID id = UUID.randomUUID();
        Publicacion publicacion = Publicacion.builder()
                .id(id)
                .titulo("Ejercicio de POO")
                .autorId(autorId)
                .carrera("Ingeniería en Informática")
                .tipo(TipoRecurso.EJERCICIO)
                .build();

        when(publicacionRepository.findById(id)).thenReturn(Optional.of(publicacion));

        assertThatThrownBy(() -> publicacionService.eliminar(id, "otro-usuario-oid"))
                .isInstanceOf(OperacionNoPermitidaException.class);

        verify(publicacionRepository, never()).delete(any());
    }
}
