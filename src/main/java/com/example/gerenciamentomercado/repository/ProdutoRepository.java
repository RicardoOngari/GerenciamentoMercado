package com.example.gerenciamentomercado.repository;

import com.example.gerenciamentomercado.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}