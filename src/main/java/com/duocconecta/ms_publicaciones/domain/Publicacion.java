package com.duocconecta.ms_publicaciones.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "publicaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(name = "autor_id", nullable = false, length = 100)
    private String autorId;

    @Column(nullable = false, length = 80)
    private String carrera;

    /** Sede/campus donde cursa el autor (DuocUC tiene varias). */
    @Column(length = 80)
    private String sede;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoRecurso tipo;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "publicacion_tags", joinColumns = @JoinColumn(name = "publicacion_id"))
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    /** Reemplaza el antiguo urlAdjunto único: ahora se pueden adjuntar varios archivos. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "publicacion_archivos", joinColumns = @JoinColumn(name = "publicacion_id"))
    @Column(name = "url_archivo")
    private List<String> archivos = new ArrayList<>();

    @Column(name = "repositorio_id")
    private UUID repositorioId;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @PrePersist
    void alPersistir() {
        this.fechaCreacion = Instant.now();
    }
}