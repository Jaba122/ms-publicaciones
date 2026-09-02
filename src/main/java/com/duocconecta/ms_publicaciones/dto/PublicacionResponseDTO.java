package com.duocconecta.ms_publicaciones.dto;

import com.duocconecta.ms_publicaciones.domain.Publicacion;
import com.duocconecta.ms_publicaciones.domain.TipoRecurso;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicacionResponseDTO(
        UUID id,
        String titulo,
        String descripcion,
        String autorId,
        String carrera,
        String sede,
        TipoRecurso tipo,
        List<String> tags,
        List<String> archivos,
        UUID repositorioId,
        Instant fechaCreacion
) {
    public static PublicacionResponseDTO desdeEntidad(Publicacion p) {
        return new PublicacionResponseDTO(
                p.getId(), p.getTitulo(), p.getDescripcion(), p.getAutorId(),
                p.getCarrera(), p.getSede(), p.getTipo(), p.getTags(), p.getArchivos(),
                p.getRepositorioId(), p.getFechaCreacion());
    }
}