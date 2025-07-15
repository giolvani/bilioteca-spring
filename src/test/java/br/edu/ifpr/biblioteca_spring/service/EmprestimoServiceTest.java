package br.edu.ifpr.biblioteca_spring.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.edu.ifpr.biblioteca_spring.models.Emprestimo;
import br.edu.ifpr.biblioteca_spring.models.Livro;
import br.edu.ifpr.biblioteca_spring.models.Usuario;

@SpringBootTest
@DisplayName("Testes do EmprestimoService")
class EmprestimoServiceTest {

    private EmprestimoService emprestimoService;
    private Usuario usuario;
    private Livro livro;

    @BeforeEach
    void setUp() {
        emprestimoService = new EmprestimoService();
        emprestimoService.limpar();

        // Cria usuário de teste
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");

        // Cria livro de teste
        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        livro.setDisponivel(true);
    }

    @Test
    @DisplayName("Deve realizar empréstimo com dados válidos")
    void deveRealizarEmprestimoComDadosValidos() {
        // Act
        Emprestimo emprestimo = emprestimoService.emprestarLivro(usuario, livro);

        // Assert
        assertNotNull(emprestimo.getId());
        assertEquals(usuario, emprestimo.getUsuario());
        assertEquals(livro, emprestimo.getLivro());
        assertEquals(LocalDate.now(), emprestimo.getDataEmprestimo());
        assertNotNull(emprestimo.getDataPrevistaDevolucao());
        assertNull(emprestimo.getDataDevolucaoReal());
        assertFalse(livro.isDisponivel()); // Livro deve estar marcado como emprestado
    }

    @Test
    @DisplayName("Deve calcular data de devolução evitando fins de semana")
    void deveCalcularDataDevolucaoEvitandoFimDeSemana() {
        // Arrange - Simula uma sexta-feira
        LocalDate sexta = LocalDate.of(2024, 1, 5); // Sexta-feira

        // Act
        LocalDate dataDevolucao = emprestimoService.calcularDataDevolucao(sexta);

        // Assert
        // Deve pular o fim de semana e ir para segunda-feira (8 de janeiro)
        assertEquals(LocalDate.of(2024, 1, 8), dataDevolucao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar para usuário nulo")
    void deveLancarExcecaoAoEmprestarParaUsuarioNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(null, livro));
        assertEquals("Usuário não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar livro nulo")
    void deveLancarExcecaoAoEmprestarLivroNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(usuario, null));
        assertEquals("Livro não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar para usuário sem ID")
    void deveLancarExcecaoAoEmprestarParaUsuarioSemId() {
        // Arrange
        usuario.setId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(usuario, livro));
        assertEquals("Usuário deve ter um ID válido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar livro sem ID")
    void deveLancarExcecaoAoEmprestarLivroSemId() {
        // Arrange
        livro.setId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(usuario, livro));
        assertEquals("Livro deve ter um ID válido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar livro indisponível")
    void deveLancarExcecaoAoEmprestarLivroIndisponivel() {
        // Arrange
        livro.setDisponivel(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(usuario, livro));
        assertEquals("Livro não está disponível para empréstimo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar para usuário bloqueado")
    void deveLancarExcecaoAoEmprestarParaUsuarioBloqueado() {
        // Arrange
        usuario.bloquearAte(LocalDate.now().plusDays(5));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(usuario, livro));
        assertTrue(exception.getMessage().contains("Usuário está bloqueado até"));
        assertTrue(exception.getMessage().contains("Não é possível realizar novos empréstimos"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário atinge limite de 3 livros")
    void deveLancarExcecaoQuandoUsuarioAtingeLimiteDe3Livros() {
        // Arrange - Cria 3 empréstimos para o usuário
        for (int i = 1; i <= 3; i++) {
            Livro livroTemp = new Livro();
            livroTemp.setId((long) i);
            livroTemp.setTitulo("Livro " + i);
            livroTemp.setAutor("Autor " + i);
            livroTemp.setDisponivel(true);
            emprestimoService.emprestarLivro(usuario, livroTemp);
        }

        // Tenta emprestar o 4º livro
        Livro quartoLivro = new Livro();
        quartoLivro.setId(4L);
        quartoLivro.setTitulo("Quarto Livro");
        quartoLivro.setAutor("Quarto Autor");
        quartoLivro.setDisponivel(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.emprestarLivro(usuario, quartoLivro));
        assertEquals("Usuário já atingiu o limite máximo de 3 livros emprestados.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve devolver livro com sucesso")
    void deveDevolverLivroComSucesso() {
        // Arrange
        Emprestimo emprestimo = emprestimoService.emprestarLivro(usuario, livro);

        // Act
        Optional<String> resultado = emprestimoService.devolverLivro(emprestimo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Livro devolvido com sucesso!", resultado.get());
        assertTrue(livro.isDisponivel());
        assertNotNull(emprestimo.getDataDevolucaoReal());
        assertEquals(LocalDate.now(), emprestimo.getDataDevolucaoReal());
    }

    @Test
    @DisplayName("Deve aplicar bloqueio por devolução em atraso")
    void deveAplicarBloqueiorPorDevolucaoEmAtraso() {
        // Arrange
        Emprestimo emprestimo = emprestimoService.emprestarLivro(usuario, livro);

        // Simula atraso alterando a data prevista para o passado
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(3));

        // Act
        Optional<String> resultado = emprestimoService.devolverLivro(emprestimo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().contains("Livro devolvido com 3 dia(s) de atraso"));
        assertTrue(resultado.get().contains("Usuário bloqueado até"));
        assertTrue(usuario.isBloqueado());

        // Verifica se o bloqueio é de 8 dias (5 base + 3 de atraso)
        LocalDate dataBloqueioEsperada = LocalDate.now().plusDays(8);
        assertEquals(dataBloqueioEsperada, usuario.getDataDeDesbloqueio());
    }

    @Test
    @DisplayName("Deve lançar exceção ao devolver com ID nulo")
    void deveLancarExcecaoAoDevolverComIdNulo() {
        // Act
        Optional<String> resultado = emprestimoService.devolverLivro(null);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("ID do empréstimo não pode ser nulo.", resultado.get());
    }

    @Test
    @DisplayName("Deve retornar erro ao devolver empréstimo inexistente")
    void deveRetornarErroAoDevolverEmprestimoInexistente() {
        // Act
        Optional<String> resultado = emprestimoService.devolverLivro(999L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Empréstimo com ID 999 não encontrado.", resultado.get());
    }

    @Test
    @DisplayName("Deve retornar erro ao devolver livro já devolvido")
    void deveRetornarErroAoDevolverLivroJaDevolvido() {
        // Arrange
        Emprestimo emprestimo = emprestimoService.emprestarLivro(usuario, livro);
        emprestimoService.devolverLivro(emprestimo.getId()); // Primeira devolução

        // Act - Tenta devolver novamente
        Optional<String> resultado = emprestimoService.devolverLivro(emprestimo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().contains("Este livro já foi devolvido em"));
    }

    @Test
    @DisplayName("Deve buscar empréstimo por ID")
    void deveBuscarEmprestimoPorId() {
        // Arrange
        Emprestimo emprestimo = emprestimoService.emprestarLivro(usuario, livro);

        // Act
        Optional<Emprestimo> resultado = emprestimoService.buscarPorId(emprestimo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(emprestimo.getId(), resultado.get().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por ID nulo")
    void deveLancarExcecaoAoBuscarPorIdNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emprestimoService.buscarPorId(null));
        assertEquals("ID não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar empréstimos ativos")
    void deveListarEmprestimosAtivos() {
        // Arrange
        Emprestimo emprestimo1 = emprestimoService.emprestarLivro(usuario, livro);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("O Cortiço");
        livro2.setAutor("Aluísio Azevedo");
        livro2.setDisponivel(true);

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria Silva");
        usuario2.setCpf("98765432100");

        Emprestimo emprestimo2 = emprestimoService.emprestarLivro(usuario2, livro2);

        // Devolve um dos empréstimos
        emprestimoService.devolverLivro(emprestimo1.getId());

        // Act
        List<Emprestimo> ativos = emprestimoService.buscarEmprestimosAtivos();

        // Assert
        assertEquals(1, ativos.size());
        assertEquals(emprestimo2.getId(), ativos.get(0).getId());
    }

    @Test
    @DisplayName("Deve listar empréstimos atrasados")
    void deveListarEmprestimosAtrasados() {
        // Arrange
        Emprestimo emprestimo1 = emprestimoService.emprestarLivro(usuario, livro);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("O Cortiço");
        livro2.setAutor("Aluísio Azevedo");
        livro2.setDisponivel(true);

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Maria Silva");
        usuario2.setCpf("98765432100");

        Emprestimo emprestimo2 = emprestimoService.emprestarLivro(usuario2, livro2);

        // Simula atraso no primeiro empréstimo
        emprestimo1.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));

        // Act
        List<Emprestimo> atrasados = emprestimoService.buscarEmprestimosAtrasados();

        // Assert
        assertEquals(1, atrasados.size());
        assertEquals(emprestimo1.getId(), atrasados.get(0).getId());
    }

    @Test
    @DisplayName("Deve verificar se usuário pode emprestar")
    void deveVerificarSeUsuarioPodeEmprestar() {
        // Act & Assert - Usuário sem empréstimos deve poder emprestar
        assertTrue(emprestimoService.podeEmprestar(usuario));

        // Arrange - Bloqueia usuário
        usuario.bloquearAte(LocalDate.now().plusDays(1));

        // Act & Assert - Usuário bloqueado não deve poder emprestar
        assertFalse(emprestimoService.podeEmprestar(usuario));
    }

    @Test
    @DisplayName("Deve contar empréstimos ativos do usuário")
    void deveContarEmprestimosAtivosDoUsuario() {
        // Arrange - Cria 2 empréstimos para o usuário
        emprestimoService.emprestarLivro(usuario, livro);

        Livro livro2 = new Livro();
        livro2.setId(2L);
        livro2.setTitulo("O Cortiço");
        livro2.setAutor("Aluísio Azevedo");
        livro2.setDisponivel(true);
        emprestimoService.emprestarLivro(usuario, livro2);

        // Act
        int count = emprestimoService.contarEmprestimosAtivos(usuario);

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve limpar lista e resetar ID generator")
    void deveLimparListaEResetarIdGenerator() {
        // Arrange
        emprestimoService.emprestarLivro(usuario, livro);

        // Act
        emprestimoService.limpar();

        // Assert
        assertEquals(0, emprestimoService.listarTodos().size());

        // Verifica se o ID generator foi resetado
        Emprestimo novoEmprestimo = emprestimoService.emprestarLivro(usuario, livro);
        assertEquals(1L, novoEmprestimo.getId());
    }
}
