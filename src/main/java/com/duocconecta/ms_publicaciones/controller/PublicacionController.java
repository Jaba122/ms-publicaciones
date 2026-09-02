package com.duocconecta.ms_publicaciones.controller;

import com.duocconecta.ms_publicaciones.domain.Publicacion;
import com.duocconecta.ms_publicaciones.domain.TipoRecurso;
import com.duocconecta.ms_publicaciones.dto.PublicacionRequestDTO;
import com.duocconecta.ms_publicaciones.dto.PublicacionResponseDTO;
import com.duocconecta.ms_publicaciones.service.PublicacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;

    /**
     * El id del usuario autenticado lo deja disponible JwtUserContextFilter
     * como atributo de request (ver cl.duocconecta.publicaciones.security).
     */
    private String usuarioActual(HttpServletRequest request) {
        return (String) request.getAttribute("currentUserId");
    }

    @PostMapping
    public ResponseEntity<PublicacionResponseDTO> crear(@Valid @RequestBody PublicacionRequestDTO dto,
                                                          HttpServletRequest request) {
        Publicacion creada = publicacionService.crear(dto, usuarioActual(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(PublicacionResponseDTO.desdeEntidad(creada));
    }

    @GetMapping
    public ResponseEntity<List<PublicacionResponseDTO>> listar(
            @RequestParam(required = false) String carrera,
            @RequestParam(required = false) TipoRecurso tipo,
            @RequestParam(required = false) String sede) {
        List<PublicacionResponseDTO> resultado = publicacionService.listar(carrera, tipo, sede).stream()
                .map(PublicacionResponseDTO::desdeEntidad)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionResponseDTO> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(PublicacionResponseDTO.desdeEntidad(publicacionService.obtenerPorId(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, HttpServletRequest request) {
        publicacionService.eliminar(id, usuarioActual(request));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ms-publicaciones activo");
    }
}
