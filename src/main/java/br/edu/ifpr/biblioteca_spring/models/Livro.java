package br.edu.ifpr.biblioteca_spring.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Livro {

    private Long id;

    @NotBlank(message = "O título é obrigatório.")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\p{Punct}]+$", message = "O título contém caracteres inválidos.")
    private String titulo;

    @NotBlank(message = "O autor é obrigatório.")
    @Size(min = 2, max = 80, message = "O nome do autor deve ter entre 2 e 80 caracteres.")
    @Pattern(regexp = "^[\\p{L}\\s\\.\\-']+$", message = "O nome do autor deve conter apenas letras, espaços, pontos, hífens e apostrofes.")
    private String autor;

    @NotNull(message = "O status de disponibilidade é obrigatório.")
    private Boolean disponivel;

    // Construtor padrão necessário para Spring
    public Livro() {
        this.disponivel = true;
    }

    public Livro(Long id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = true;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}
