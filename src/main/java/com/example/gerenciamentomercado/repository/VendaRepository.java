package com.example.gerenciamentomercado.repository;

import com.example.gerenciamentomercado.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepository extends JpaRepository<Venda, Long> {
}