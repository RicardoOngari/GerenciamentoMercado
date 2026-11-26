
package com.example.gerenciamentomercado.service;

import com.example.gerenciamentomercado.model.ItemVenda;
import com.example.gerenciamentomercado.model.Produto;
import com.example.gerenciamentomercado.model.Venda;
import com.example.gerenciamentomercado.repository.ProdutoRepository;
import com.example.gerenciamentomercado.repository.VendaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository,
                        ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Venda não encontrada"));
    }

    public Venda registrarVenda(Venda venda) {

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A venda precisa ter pelo menos um item");
        }

        for (ItemVenda item : venda.getItens()) {

            if (item.getProduto() == null ||
                    item.getProduto().getId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Informe o produto da venda");
            }

            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Quantidade inválida");
            }

            Produto produto = produtoRepository.findById(
                    item.getProduto().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produto não encontrado"));

            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidadeEstoque(
                    produto.getQuantidadeEstoque() - item.getQuantidade()
            );

            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());
            item.setVenda(venda);
        }

        return vendaRepository.save(venda);
    }
}
