
package com.example.gerenciamentomercado.service;

import com.example.gerenciamentomercado.model.ItemVenda;
import com.example.gerenciamentomercado.model.Produto;
import com.example.gerenciamentomercado.model.Venda;
import com.example.gerenciamentomercado.repository.ProdutoRepository;
import com.example.gerenciamentomercado.repository.VendaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private VendaService vendaService;

    @Test
    void deveRegistrarVendaEAtualizarEstoque() {

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz");
        produto.setPreco(new BigDecimal("25.90"));
        produto.setQuantidadeEstoque(10);

        Produto produtoRecebido = new Produto();
        produtoRecebido.setId(1L);

        ItemVenda item = new ItemVenda();
        item.setProduto(produtoRecebido);
        item.setQuantidade(3);

        Venda venda = new Venda();
        venda.getItens().add(item);

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(vendaRepository.save(any(Venda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Venda resultado = vendaService.registrarVenda(venda);

        assertNotNull(resultado);
        assertEquals(7, produto.getQuantidadeEstoque());
        assertEquals(new BigDecimal("25.90"), item.getPrecoUnitario());

        verify(produtoRepository, times(1)).findById(1L);
        verify(vendaRepository, times(1)).save(venda);
    }

    @Test
    void naoDeveRegistrarVendaSemItens() {

        Venda venda = new Venda();

        assertThrows(ResponseStatusException.class,
                () -> vendaService.registrarVenda(venda));

        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComEstoqueInsuficiente() {

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz");
        produto.setQuantidadeEstoque(2);

        Produto produtoRecebido = new Produto();
        produtoRecebido.setId(1L);

        ItemVenda item = new ItemVenda();
        item.setProduto(produtoRecebido);
        item.setQuantidade(5);

        Venda venda = new Venda();
        venda.getItens().add(item);

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        assertThrows(ResponseStatusException.class,
                () -> vendaService.registrarVenda(venda));

        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComProdutoInexistente() {

        Produto produto = new Produto();
        produto.setId(99L);

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(1);

        Venda venda = new Venda();
        venda.getItens().add(item);

        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> vendaService.registrarVenda(venda));

        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeZero() {

        Produto produto = new Produto();
        produto.setId(1L);

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(0);

        Venda venda = new Venda();
        venda.getItens().add(item);

        assertThrows(ResponseStatusException.class,
                () -> vendaService.registrarVenda(venda));

        verify(produtoRepository, never()).findById(any());
        verify(vendaRepository, never()).save(any(Venda.class));
    }
}
