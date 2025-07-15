package br.edu.ifpr.biblioteca_spring.service;

import org.springframework.stereotype.Service;

import br.edu.ifpr.biblioteca_spring.models.Emprestimo;
import br.edu.ifpr.biblioteca_spring.models.Livro;
import br.edu.ifpr.biblioteca_spring.models.Usuario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmprestimoService {

    private static final List<Emprestimo> emprestimos = new ArrayList<>();
    private static final AtomicLong idGenerator = new AtomicLong();

    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(emprestimos);
    }

    public List<Emprestimo> Usuario(Usuario usuario) {
        List<Emprestimo> resultado = new ArrayList<>();
        for (Emprestimo e : emprestimos) {
            if (e.getUsuario().getId().equals(usuario.getId()) && e.getDataDevolucaoReal() == null) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public boolean podeEmprestar(Usuario usuario) {
        if (usuario.isBloqueado()) {
            return false;
        }

        int contador = 0;
        for (Emprestimo e : emprestimos) {
            if (e.getUsuario().getId().equals(usuario.getId()) && e.getDataDevolucaoReal() == null) {
                contador++;
            }
        }

        return contador < 3;
    }

    public LocalDate calcularDataDevolucao(LocalDate dataInicial) {
        LocalDate data = dataInicial.plusDays(7);
        while (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            data = data.plusDays(1);
        }
        return data;
    }

    public Emprestimo emprestarLivro(Usuario usuario, Livro livro) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }

        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo.");
        }

        if (usuario.getId() == null) {
            throw new IllegalArgumentException("Usuário deve ter um ID válido.");
        }

        if (livro.getId() == null) {
            throw new IllegalArgumentException("Livro deve ter um ID válido.");
        }

        if (!podeEmprestar(usuario)) {
            if (usuario.isBloqueado()) {
                throw new IllegalArgumentException("Usuário está bloqueado até " +
                        usuario.getDataDeDesbloqueio().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        ". Não é possível realizar novos empréstimos.");
            } else {
                throw new IllegalArgumentException("Usuário já atingiu o limite máximo de 3 livros emprestados.");
            }
        }

        if (!livro.isDisponivel()) {
            throw new IllegalArgumentException("Livro não está disponível para empréstimo.");
        }

        livro.setDisponivel(false);
        LocalDate hoje = LocalDate.now();
        LocalDate devolucao = calcularDataDevolucao(hoje);

        Emprestimo emp = new Emprestimo(
                idGenerator.incrementAndGet(), usuario, livro, hoje, devolucao);

        emprestimos.add(emp);
        return emp;
    }

    public Optional<String> devolverLivro(Long emprestimoId) {
        if (emprestimoId == null) {
            return Optional.of("ID do empréstimo não pode ser nulo.");
        }

        Emprestimo encontrado = null;

        for (Emprestimo e : emprestimos) {
            if (e.getId().equals(emprestimoId)) {
                encontrado = e;
                break;
            }
        }

        if (encontrado == null) {
            return Optional.of("Empréstimo com ID " + emprestimoId + " não encontrado.");
        }

        if (encontrado.getDataDevolucaoReal() != null) {
            return Optional.of("Este livro já foi devolvido em " +
                    encontrado.getDataDevolucaoReal().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ".");
        }

        encontrado.setDataDevolucaoReal(LocalDate.now());
        encontrado.getLivro().setDisponivel(true);

        // Verifica atraso e aplica bloqueio se necessário
        if (encontrado.getDataDevolucaoReal().isAfter(encontrado.getDataPrevistaDevolucao())) {
            long diasAtraso = encontrado.getDataPrevistaDevolucao()
                    .until(encontrado.getDataDevolucaoReal()).getDays();
            long diasBloqueio = 5 + diasAtraso; // 5 dias base + 1 dia por dia de atraso

            LocalDate dataBloqueio = LocalDate.now().plusDays(diasBloqueio);
            encontrado.getUsuario().bloquearAte(dataBloqueio);

            return Optional.of("Livro devolvido com " + diasAtraso + " dia(s) de atraso. " +
                    "Usuário bloqueado até "
                    + dataBloqueio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
        }

        return Optional.of("Livro devolvido com sucesso!");
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }

        for (Emprestimo e : emprestimos) {
            if (e.getId().equals(id)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public List<Emprestimo> buscarEmprestimosAtivos() {
        List<Emprestimo> ativos = new ArrayList<>();
        for (Emprestimo e : emprestimos) {
            if (e.getDataDevolucaoReal() == null) {
                ativos.add(e);
            }
        }
        return ativos;
    }

    public List<Emprestimo> buscarEmprestimosAtrasados() {
        List<Emprestimo> atrasados = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        for (Emprestimo e : emprestimos) {
            if (e.getDataDevolucaoReal() == null &&
                    e.getDataPrevistaDevolucao().isBefore(hoje)) {
                atrasados.add(e);
            }
        }
        return atrasados;
    }

    public int contarEmprestimosAtivos(Usuario usuario) {
        int contador = 0;
        for (Emprestimo e : emprestimos) {
            if (e.getUsuario().getId().equals(usuario.getId()) && e.getDataDevolucaoReal() == null) {
                contador++;
            }
        }
        return contador;
    }

    public void limpar() {
        emprestimos.clear();
        idGenerator.set(0);
    }
}
