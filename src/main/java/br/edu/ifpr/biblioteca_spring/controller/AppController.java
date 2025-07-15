package br.edu.ifpr.biblioteca_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AppController {

    @RequestMapping({"", "/"})
    public String home() {
        return "redirect:/emprestimos";
    }
    
}
