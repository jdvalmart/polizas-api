package com.segurosbolivar.polizasapi.repository;

import com.segurosbolivar.polizasapi.domain.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
}
