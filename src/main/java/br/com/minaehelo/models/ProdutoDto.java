package br.com.minaehelo.models;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class ProdutoDto {
    @NotEmpty(message = "O nome é obrigatório")
    private String nome;

    @NotEmpty(message = "A categoria é obrigatória")
    private String categoria;

    @Min(0)
    private double preco;

    @Size(min = 10, message = "A descrição deve ter no mínimo 10 caracteres")
    @Size(max = 2000, message = "A descrição não pode exceder 2000 caracteres")
    private String descricao;

    private MultipartFile nomeImagem;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MultipartFile getNomeImagem() {
        return nomeImagem;
    }

    public void setNomeImagem(MultipartFile nomeImagem) {
        this.nomeImagem = nomeImagem;
    }
}
