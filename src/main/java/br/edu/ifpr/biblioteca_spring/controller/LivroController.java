package br.edu.ifpr.biblioteca_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpr.biblioteca_spring.models.Livro;
import br.edu.ifpr.biblioteca_spring.service.LivroService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livroService.listarTodos());
        return "livros/lista";
    }

    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        model.addAttribute("livros", livroService.listarDisponiveis());
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String formularioNovoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "livros/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Livro livro, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            // Se houver erros de validação, retorna para o formulário mantendo os dados
            model.addAttribute("livro", livro);
            return "livros/form";
        }
        
        try {        
            livroService.adicionar(livro);
            redirectAttrs.addFlashAttribute("mensagemSucesso", "Livro cadastrado com sucesso!");
            return "redirect:/livros";
        } catch (IllegalArgumentException e) {
            // Erro de negócio (ex: livro duplicado)
            model.addAttribute("livro", livro);
            model.addAttribute("mensagemErro", e.getMessage());
            return "livros/form";
        } catch (Exception e) {
            // Erro inesperado
            model.addAttribute("livro", livro);
            model.addAttribute("mensagemErro", "Erro inesperado ao cadastrar livro. Tente novamente.");
            return "livros/form";
        }
    }
}
