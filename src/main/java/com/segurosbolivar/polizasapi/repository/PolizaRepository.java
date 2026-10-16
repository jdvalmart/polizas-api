package com.segurosbolivar.polizasapi.repository;

import com.segurosbolivar.polizasapi.domain.EstadoPoliza;
import com.segurosbolivar.polizasapi.domain.Poliza;
import com.segurosbolivar.polizasapi.domain.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    @Query("select p from Poliza p where (:tipo is null or p.tipo = :tipo) and (:estado is null or p.estado = :estado)")
    List<Poliza> listar(@Param("tipo") TipoPoliza tipo, @Param("estado") EstadoPoliza estado);
}
