package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.EstadoOC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoOCRepository extends JpaRepository<EstadoOC, Integer> {
}
