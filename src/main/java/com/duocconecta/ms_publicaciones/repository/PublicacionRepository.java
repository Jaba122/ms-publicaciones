package com.duocconecta.ms_publicaciones.repository;

import com.duocconecta.ms_publicaciones.domain.Publicacion;
import com.duocconecta.ms_publicaciones.domain.TipoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PublicacionRepository extends JpaRepository<Publicacion, UUID> {

    List<Publicacion> findByCarreraIgnoreCase(String carrera);

    List<Publicacion> findByTipo(TipoRecurso tipo);

    List<Publicacion> findByCarreraIgnoreCaseAndTipo(String carrera, TipoRecurso tipo);

    List<Publicacion> findBySedeIgnoreCase(String sede);

    List<Publicacion> findByAutorId(String autorId);
}