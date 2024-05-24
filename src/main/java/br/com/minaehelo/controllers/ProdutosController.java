package br.com.minaehelo.controllers;

import br.com.minaehelo.models.ProdutoDto;
import br.com.minaehelo.models.Produtos;
import br.com.minaehelo.services.ProdutosRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.*;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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

    @GetMapping("/cadastrarProduto")
    public String mostrarPaginaCreate(Model model) {
        ProdutoDto produtoDto = new ProdutoDto();
        model.addAttribute("produtoDto", produtoDto);
        return "produtos/cadastrarProduto";
    }

    @PostMapping("/cadastrarProduto")
    public String cadastrarProduto(
            @Valid @ModelAttribute ProdutoDto produtoDto, BindingResult result
            ) throws IOException {

        if (produtoDto.getNomeImagem().isEmpty()){
            result.addError(new FieldError("productDto", "nomeImagem", "A imagem é obrigatória"));
        }

        if (result.hasErrors()){
            return "produtos/cadastrarProduto";
        }

        //salvar imagem
        MultipartFile imagem = produtoDto.getNomeImagem();
        Date dtCriacao = new Date();
        String guardarImagem = dtCriacao.getTime() + "_" + imagem.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imagem.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + guardarImagem), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }

        Produtos produto = new Produtos();
        produto.setNome(produtoDto.getNome());
        produto.setDescricao(produtoDto.getDescricao());
        produto.setCategoria(produtoDto.getCategoria());
        produto.setPreco(produtoDto.getPreco());
        produto.setDtCriacao(dtCriacao);
        produto.setNomeImagem(guardarImagem);

        repo.save(produto);

        return "redirect:/produtos";
    }

    @GetMapping("/editar")
    public String mostrarPaginaEditar(
            Model model, @RequestParam int id
            ) {

        try {
            Produtos produto = repo.findById(id).get();
            model.addAttribute("produto", produto);

            ProdutoDto produtoDto = new ProdutoDto();
            produtoDto.setNome(produto.getNome());
            produtoDto.setDescricao(produto.getDescricao());
            produtoDto.setCategoria(produto.getCategoria());
            produtoDto.setPreco(produto.getPreco());

            model.addAttribute("produtoDto", produtoDto);
        }
        catch (Exception ex) {
            System.out.println("Exceção: " + ex.getMessage());
            return "redirect:/produtos";
        }
        return "produtos/editarProduto";
    }

    @PostMapping("/editar")
    public String updateProduto (
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProdutoDto produtoDto,
            BindingResult result
            ) {

        try {
            Produtos produto = repo.findById(id).get();
            model.addAttribute("produto", produto);

            if (result.hasErrors()) {
                return "produtos/editarProduto";
            }

            if (!produtoDto.getNomeImagem().isEmpty()) {
                //deleta a imagem antiga
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + produto.getNomeImagem());

                try {
                    Files.delete(oldImagePath);
                }
                catch (Exception ex) {
                    System.out.println("Exceção: " + ex.getMessage());
                }

                //salvar nova imagem
                MultipartFile imagem = produtoDto.getNomeImagem();
                Date dtCriacao = new Date();
                String guardarImagem = dtCriacao.getTime() + "_" + imagem.getOriginalFilename();

                try (InputStream inputStream = imagem.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + guardarImagem), StandardCopyOption.REPLACE_EXISTING);
                }
                produto.setNomeImagem(guardarImagem);
            }

            produto.setNome(produtoDto.getNome());
            produto.setDescricao(produtoDto.getDescricao());
            produto.setCategoria(produtoDto.getCategoria());
            produto.setPreco(produtoDto.getPreco());

            repo.save(produto);
        }
        catch (Exception ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }
        return "redirect:/produtos";
    }

    @GetMapping("/deletar")
    public String deletarProduto (
            @RequestParam int id
            ) {

        try {
            Produtos produto = repo.findById(id).get();

            //deletar imagem do produto
            Path imagemPath = Paths.get("public/images/" + produto.getNomeImagem());

            try {
                Files.delete(imagemPath);
            }
            catch (Exception ex) {
                System.out.println("Exceção: " + ex.getMessage());
            }

            //deletar o produto
            repo.delete(produto);
        }
        catch (Exception ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }

        return  "redirect:/produtos";
    }
}
