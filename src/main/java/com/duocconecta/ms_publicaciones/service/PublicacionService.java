package com.duocconecta.ms_publicaciones.service;

import com.duocconecta.ms_publicaciones.domain.Publicacion;
import com.duocconecta.ms_publicaciones.domain.TipoRecurso;
import com.duocconecta.ms_publicaciones.dto.PublicacionRequestDTO;
import com.duocconecta.ms_publicaciones.exception.OperacionNoPermitidaException;
import com.duocconecta.ms_publicaciones.exception.RecursoNoEncontradoException;
import com.duocconecta.ms_publicaciones.repository.PublicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;

    public Publicacion crear(PublicacionRequestDTO dto, String autorId) {
        Publicacion publicacion = Publicacion.builder()
                .titulo(dto.titulo())
                .descripcion(dto.descripcion())
                .autorId(autorId)
                .carrera(dto.carrera())
                .sede(dto.sede())
                .tipo(dto.tipo())
                .tags(dto.tags() != null ? dto.tags() : List.of())
                .archivos(dto.archivos() != null ? dto.archivos() : List.of())
                .repositorioId(dto.repositorioId())
                .build();
        return publicacionRepository.save(publicacion);
    }

    public List<Publicacion> listar(String carrera, TipoRecurso tipo, String sede) {
        boolean tieneCarrera = StringUtils.hasText(carrera);
        boolean tieneTipo = tipo != null;
        boolean tieneSede = StringUtils.hasText(sede);

        // Filtro simple por ahora; si se combinan más de dos criterios a la vez,
        // conviene migrar a Specification/Criteria API en vez de seguir agregando métodos.
        if (tieneCarrera && tieneTipo) {
            return publicacionRepository.findByCarreraIgnoreCaseAndTipo(carrera, tipo);
        }
        if (tieneSede) {
            return publicacionRepository.findBySedeIgnoreCase(sede);
        }
        if (tieneCarrera) {
            return publicacionRepository.findByCarreraIgnoreCase(carrera);
        }
        if (tieneTipo) {
            return publicacionRepository.findByTipo(tipo);
        }
        return publicacionRepository.findAll();
    }

    public Publicacion obtenerPorId(UUID id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada: " + id));
    }

    public void eliminar(UUID id, String usuarioId) {
        Publicacion publicacion = obtenerPorId(id);
        if (!publicacion.getAutorId().equals(usuarioId)) {
            throw new OperacionNoPermitidaException("Solo el autor puede eliminar esta publicación");
        }
        publicacionRepository.delete(publicacion);
    }
}