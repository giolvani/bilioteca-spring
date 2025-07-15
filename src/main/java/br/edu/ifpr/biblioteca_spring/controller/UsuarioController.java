package br.edu.ifpr.biblioteca_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpr.biblioteca_spring.models.Usuario;
import br.edu.ifpr.biblioteca_spring.service.UsuariosService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuariosService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String cadastrar(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model,
            RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("erro", "Por favor, corrija os erros abaixo.");
            return "usuarios/form";
        }

        try {
            usuarioService.adicionar(usuario);
            redirectAttrs.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("erro", e.getMessage());
            return "usuarios/form";
        }
    }
}
