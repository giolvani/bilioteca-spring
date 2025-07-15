package br.edu.ifpr.biblioteca_spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import br.edu.ifpr.biblioteca_spring.models.Livro;

@Service
public class LivroService {

    private static final List<Livro> livros = new ArrayList<>();
    private static final AtomicLong idGenerator = new AtomicLong();

    public List<Livro> listarTodos() {
        return new ArrayList<>(livros);
    }

    public List<Livro> listarDisponiveis() {
        List<Livro> disponiveis = new ArrayList<>();
        for (Livro l : livros) {
            if (l.isDisponivel()) {
                disponiveis.add(l);
            }
        }
        return disponiveis;
    }

    public Optional<Livro> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }

        for (Livro l : livros) {
            if (l.getId().equals(id)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    public Optional<Livro> buscarPorTituloEAutor(String titulo, String autor) {
        if (titulo == null || titulo.trim().isEmpty() || autor == null || autor.trim().isEmpty()) {
            return Optional.empty();
        }

        String tituloLimpo = titulo.trim().toLowerCase();
        String autorLimpo = autor.trim().toLowerCase();

        for (Livro l : livros) {
            if (l.getTitulo().trim().toLowerCase().equals(tituloLimpo) &&
                    l.getAutor().trim().toLowerCase().equals(autorLimpo)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    public boolean existeLivro(String titulo, String autor) {
        return buscarPorTituloEAutor(titulo, autor).isPresent();
    }

    public Livro adicionar(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo.");
        }

        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }

        if (livro.getAutor() == null || livro.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("Autor é obrigatório.");
        }

        // Verifica se já existe um livro com mesmo título e autor
        if (existeLivro(livro.getTitulo(), livro.getAutor())) {
            throw new IllegalArgumentException("Já existe um livro cadastrado com este título e autor.");
        }

        // Normaliza os dados
        livro.setTitulo(livro.getTitulo().trim());
        livro.setAutor(livro.getAutor().trim());
        livro.setId(idGenerator.incrementAndGet());

        if (livro.isDisponivel() == null) {
            livro.setDisponivel(true);
        }

        livros.add(livro);
        return livro;
    }

    public Livro atualizar(Livro livro) {
        if (livro == null || livro.getId() == null) {
            throw new IllegalArgumentException("Livro e ID são obrigatórios para atualização.");
        }

        Optional<Livro> livroExistente = buscarPorId(livro.getId());
        if (livroExistente.isEmpty()) {
            throw new IllegalArgumentException("Livro com ID " + livro.getId() + " não encontrado.");
        }

        // Verifica se já existe outro livro com mesmo título e autor
        Optional<Livro> livroComMesmoTituloAutor = buscarPorTituloEAutor(livro.getTitulo(), livro.getAutor());
        if (livroComMesmoTituloAutor.isPresent() && !livroComMesmoTituloAutor.get().getId().equals(livro.getId())) {
            throw new IllegalArgumentException("Já existe outro livro cadastrado com este título e autor.");
        }

        // Atualiza os dados
        Livro l = livroExistente.get();
        l.setTitulo(livro.getTitulo().trim());
        l.setAutor(livro.getAutor().trim());

        return l;
    }

    public void marcarComoEmprestado(Long livroId) {
        if (livroId == null) {
            throw new IllegalArgumentException("ID do livro não pode ser nulo.");
        }

        Optional<Livro> livro = buscarPorId(livroId);
        if (livro.isEmpty()) {
            throw new IllegalArgumentException("Livro com ID " + livroId + " não encontrado.");
        }

        if (!livro.get().isDisponivel()) {
            throw new IllegalArgumentException("Livro já está emprestado.");
        }

        livro.get().setDisponivel(false);
    }

    public void marcarComoDisponivel(Long livroId) {
        if (livroId == null) {
            throw new IllegalArgumentException("ID do livro não pode ser nulo.");
        }

        Optional<Livro> livro = buscarPorId(livroId);
        if (livro.isEmpty()) {
            throw new IllegalArgumentException("Livro com ID " + livroId + " não encontrado.");
        }

        livro.get().setDisponivel(true);
    }

    public void limpar() {
        livros.clear();
        idGenerator.set(0);
    }
}
