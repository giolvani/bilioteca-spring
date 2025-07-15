package br.edu.ifpr.biblioteca_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpr.biblioteca_spring.models.Livro;
import br.edu.ifpr.biblioteca_spring.models.Usuario;
import br.edu.ifpr.biblioteca_spring.service.EmprestimoService;
import br.edu.ifpr.biblioteca_spring.service.LivroService;
import br.edu.ifpr.biblioteca_spring.service.UsuariosService;

import java.util.Optional;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private LivroService livrosService;

    @Autowired
    private UsuariosService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("emprestimos", emprestimoService.listarTodos());
        return "emprestimos/lista";
    }

    @GetMapping("/novo")
    public String formularioNovoEmprestimo(Model model) {
        model.addAttribute("livros", livrosService.listarTodos());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "emprestimos/form";
    }

    @PostMapping
    public String emprestar(@RequestParam(required = false) Long usuarioId, 
                           @RequestParam(required = false) Long livroId, 
                           Model model, RedirectAttributes redirectAttrs) {

        // Validação de parâmetros
        if (usuarioId == null || livroId == null) {
            model.addAttribute("erro", "Usuário e livro devem ser selecionados.");
            model.addAttribute("selectedUsuarioId", usuarioId);
            model.addAttribute("selectedLivroId", livroId);
            model.addAttribute("livros", livrosService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            return "emprestimos/form";
        }

        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);
        Optional<Livro> livro = livrosService.buscarPorId(livroId);

        if (usuario.isPresent() && livro.isPresent()) {
            try {
                emprestimoService.emprestarLivro(usuario.get(), livro.get());
                redirectAttrs.addFlashAttribute("sucesso", "Empréstimo realizado com sucesso!");
                return "redirect:/emprestimos";
            } catch (IllegalArgumentException e) {
                model.addAttribute("erro", e.getMessage());
                model.addAttribute("selectedUsuarioId", usuarioId);
                model.addAttribute("selectedLivroId", livroId);
                model.addAttribute("livros", livrosService.listarTodos());
                model.addAttribute("usuarios", usuarioService.listarTodos());
                return "emprestimos/form";
            }
        } else {
            String mensagemErro = "Erro: ";
            if (usuario.isEmpty()) {
                mensagemErro += "Usuário não encontrado. ";
            }
            if (livro.isEmpty()) {
                mensagemErro += "Livro não encontrado.";
            }

            model.addAttribute("erro", mensagemErro);
            model.addAttribute("selectedUsuarioId", usuarioId);
            model.addAttribute("selectedLivroId", livroId);
            model.addAttribute("livros", livrosService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            return "emprestimos/form";
        }
    }

    @GetMapping("/devolucao")
    public String devolver(@RequestParam Long emprestimoId, RedirectAttributes redirectAttrs) {
        if (emprestimoId == null) {
            redirectAttrs.addFlashAttribute("erro", "ID do empréstimo é obrigatório.");
            return "redirect:/emprestimos";
        }

        Optional<String> resultado = emprestimoService.devolverLivro(emprestimoId);
        if (resultado.isPresent()) {
            String mensagem = resultado.get();
            if (mensagem.contains("sucesso")) {
                redirectAttrs.addFlashAttribute("sucesso", mensagem);
            } else {
                redirectAttrs.addFlashAttribute("erro", mensagem);
            }
        }
        return "redirect:/emprestimos";
    }
}
