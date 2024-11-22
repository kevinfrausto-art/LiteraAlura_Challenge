package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoresRepository extends JpaRepository<Autor,Long> {
    Autor findBynombreAutorIgnoreCase(String nombreAutor);
    List<Autor> findByFechaDeNacimientoLessThanEqualAndFechaFallecimientoGreaterThanEqual(int anoInicial,int anoFinal);

}
