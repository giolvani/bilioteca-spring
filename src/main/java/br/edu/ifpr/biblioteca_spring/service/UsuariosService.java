package br.edu.ifpr.biblioteca_spring.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.biblioteca_spring.models.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuariosService {

    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final AtomicLong idGenerator = new AtomicLong();

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }

        for (Usuario u : usuarios) {
            if (u.getId().equals(id)) {
                return Optional.of(u);
            }
        }
        throw new IllegalArgumentException("Usuário com ID " + id + " não encontrado.");
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return Optional.empty();
        }

        String cpfLimpo = cpf.replaceAll("\\D", ""); // Remove caracteres não numéricos

        for (Usuario u : usuarios) {
            if (u.getCpf().equals(cpfLimpo)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public boolean existeCpf(String cpf) {
        return buscarPorCpf(cpf).isPresent();
    }

    public Usuario adicionar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }

        if (usuario.getCpf() == null || usuario.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }

        // Verifica se CPF já existe
        String cpfLimpo = usuario.getCpf().replaceAll("\\D", "");
        if (existeCpf(cpfLimpo)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este CPF.");
        }

        // Garante que o CPF seja armazenado apenas com números
        usuario.setCpf(cpfLimpo);
        usuario.setId(idGenerator.incrementAndGet());
        usuarios.add(usuario);
        return usuario;
    }

    public Usuario atualizar(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Usuário e ID são obrigatórios para atualização.");
        }

        Optional<Usuario> usuarioExistente = Optional.empty();
        for (Usuario u : usuarios) {
            if (u.getId().equals(usuario.getId())) {
                usuarioExistente = Optional.of(u);
                break;
            }
        }

        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuário com ID " + usuario.getId() + " não encontrado.");
        }

        // Verifica se o CPF já existe em outro usuário
        String cpfLimpo = usuario.getCpf().replaceAll("\\D", "");
        Optional<Usuario> usuarioComMesmoCpf = buscarPorCpf(cpfLimpo);
        if (usuarioComMesmoCpf.isPresent() && !usuarioComMesmoCpf.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Já existe outro usuário cadastrado com este CPF.");
        }

        // Atualiza os dados
        Usuario u = usuarioExistente.get();
        u.setNome(usuario.getNome());
        u.setCpf(cpfLimpo);

        return u;
    }

    public void limpar() {
        usuarios.clear();
        idGenerator.set(0);
    }
}
