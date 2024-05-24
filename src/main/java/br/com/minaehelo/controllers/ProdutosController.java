package br.com.minaehelo.controllers;

import br.com.minaehelo.models.ProdutoDto;
import br.com.minaehelo.models.Produtos;
import br.com.minaehelo.services.ProdutosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutosController {

    @Autowired
    private ProdutosRepository repo;

    @GetMapping({"", "/"})
    public String mostrarListaProdutos(Model model) {
        List<Produtos> produtos = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("produtos", produtos);
        return "produtos/index";
    }

    @GetMapping("/criar")
    public String mostrarPaginaCreate(Model model) {
        ProdutoDto produtoDto = new ProdutoDto();
        model.addAttribute("produtoDto", produtoDto);
        return "produtos/criarProduto";
    }
}
