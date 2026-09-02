package com.duocconecta.ms_publicaciones.dto;

import com.duocconecta.ms_publicaciones.domain.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Lo que el cliente envía al crear una publicación.
 * No incluye autorId ni fechaCreacion: esos se resuelven en el backend
 * a partir del JWT y de la lógica de negocio, nunca desde el request.
 */
public record PublicacionRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String titulo,

        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String descripcion,

        @NotBlank(message = "La carrera es obligatoria")
        String carrera,

        String sede,

        @NotNull(message = "El tipo de recurso es obligatorio")
        TipoRecurso tipo,

        List<String> tags,

        List<String> archivos,

        UUID repositorioId
) {
}