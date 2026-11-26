package com.example.gerenciamentomercado.repository;

import com.example.gerenciamentomercado.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}