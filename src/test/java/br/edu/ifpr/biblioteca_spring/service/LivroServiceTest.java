package br.edu.ifpr.biblioteca_spring.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.edu.ifpr.biblioteca_spring.models.Livro;

@SpringBootTest
@DisplayName("Testes do LivroService")
class LivroServiceTest {

    private LivroService livroService;

    @BeforeEach
    void setUp() {
        livroService = new LivroService();
        livroService.limpar(); // Garante estado limpo para cada teste
    }

    @Test
    @DisplayName("Deve adicionar livro com dados válidos")
    void deveAdicionarLivroComDadosValidos() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");

        // Act
        Livro livroSalvo = livroService.adicionar(livro);

        // Assert
        assertNotNull(livroSalvo.getId());
        assertEquals("Dom Casmurro", livroSalvo.getTitulo());
        assertEquals("Machado de Assis", livroSalvo.getAutor());
        assertTrue(livroSalvo.isDisponivel());
        assertEquals(1, livroService.listarTodos().size());
    }

    @Test
    @DisplayName("Deve normalizar título e autor ao adicionar")
    void deveNormalizarTituloEAutorAoAdicionar() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("  Dom Casmurro  ");
        livro.setAutor("  Machado de Assis  ");

        // Act
        Livro livroSalvo = livroService.adicionar(livro);

        // Assert
        assertEquals("Dom Casmurro", livroSalvo.getTitulo());
        assertEquals("Machado de Assis", livroSalvo.getAutor());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar livro nulo")
    void deveLancarExcecaoAoAdicionarLivroNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionar(null));
        assertEquals("Livro não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar livro com título nulo")
    void deveLancarExcecaoAoAdicionarLivroComTituloNulo() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo(null);
        livro.setAutor("Machado de Assis");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionar(livro));
        assertEquals("Título é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar livro com título vazio")
    void deveLancarExcecaoAoAdicionarLivroComTituloVazio() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("   ");
        livro.setAutor("Machado de Assis");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionar(livro));
        assertEquals("Título é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar livro com autor nulo")
    void deveLancarExcecaoAoAdicionarLivroComAutorNulo() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionar(livro));
        assertEquals("Autor é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar livro duplicado")
    void deveLancarExcecaoAoAdicionarLivroDuplicado() {
        // Arrange
        Livro livro1 = new Livro();
        livro1.setTitulo("Dom Casmurro");
        livro1.setAutor("Machado de Assis");
        livroService.adicionar(livro1);

        Livro livro2 = new Livro();
        livro2.setTitulo("Dom Casmurro");
        livro2.setAutor("Machado de Assis");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionar(livro2));
        assertEquals("Já existe um livro cadastrado com este título e autor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ignorar case ao verificar duplicatas")
    void deveIgnorarCaseAoVerificarDuplicatas() {
        // Arrange
        Livro livro1 = new Livro();
        livro1.setTitulo("Dom Casmurro");
        livro1.setAutor("Machado de Assis");
        livroService.adicionar(livro1);

        Livro livro2 = new Livro();
        livro2.setTitulo("dom casmurro");
        livro2.setAutor("MACHADO DE ASSIS");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionar(livro2));
        assertEquals("Já existe um livro cadastrado com este título e autor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar livro por ID válido")
    void deveBuscarLivroPorIdValido() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        Livro livroSalvo = livroService.adicionar(livro);

        // Act
        Optional<Livro> resultado = livroService.buscarPorId(livroSalvo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Dom Casmurro", resultado.get().getTitulo());
    }

    @Test
    @DisplayName("Deve retornar empty ao buscar por ID inexistente")
    void deveRetornarEmptyAoBuscarPorIdInexistente() {
        // Act
        Optional<Livro> resultado = livroService.buscarPorId(999L);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por ID nulo")
    void deveLancarExcecaoAoBuscarPorIdNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.buscarPorId(null));
        assertEquals("ID não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar apenas livros disponíveis")
    void deveListarApenasLivrosDisponiveis() {
        // Arrange
        Livro livro1 = new Livro();
        livro1.setTitulo("Dom Casmurro");
        livro1.setAutor("Machado de Assis");
        Livro livroSalvo1 = livroService.adicionar(livro1);

        Livro livro2 = new Livro();
        livro2.setTitulo("O Cortiço");
        livro2.setAutor("Aluísio Azevedo");
        livroService.adicionar(livro2);

        // Marca um livro como emprestado
        livroService.marcarComoEmprestado(livroSalvo1.getId());

        // Act
        List<Livro> disponiveis = livroService.listarDisponiveis();

        // Assert
        assertEquals(1, disponiveis.size());
        assertEquals("O Cortiço", disponiveis.get(0).getTitulo());
    }

    @Test
    @DisplayName("Deve marcar livro como emprestado")
    void deveMarcarLivroComoEmprestado() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        Livro livroSalvo = livroService.adicionar(livro);

        // Act
        livroService.marcarComoEmprestado(livroSalvo.getId());

        // Assert
        Optional<Livro> livroAtualizado = livroService.buscarPorId(livroSalvo.getId());
        assertTrue(livroAtualizado.isPresent());
        assertFalse(livroAtualizado.get().isDisponivel());
    }

    @Test
    @DisplayName("Deve lançar exceção ao marcar livro já emprestado")
    void deveLancarExcecaoAoMarcarLivroJaEmprestado() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        Livro livroSalvo = livroService.adicionar(livro);
        livroService.marcarComoEmprestado(livroSalvo.getId());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.marcarComoEmprestado(livroSalvo.getId()));
        assertEquals("Livro já está emprestado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve marcar livro como disponível")
    void deveMarcarLivroComoDisponivel() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        Livro livroSalvo = livroService.adicionar(livro);
        livroService.marcarComoEmprestado(livroSalvo.getId());

        // Act
        livroService.marcarComoDisponivel(livroSalvo.getId());

        // Assert
        Optional<Livro> livroAtualizado = livroService.buscarPorId(livroSalvo.getId());
        assertTrue(livroAtualizado.isPresent());
        assertTrue(livroAtualizado.get().isDisponivel());
    }

    @Test
    @DisplayName("Deve atualizar livro existente")
    void deveAtualizarLivroExistente() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        Livro livroSalvo = livroService.adicionar(livro);

        // Act
        livroSalvo.setTitulo("Dom Casmurro - Edição Revisada");
        Livro livroAtualizado = livroService.atualizar(livroSalvo);

        // Assert
        assertEquals("Dom Casmurro - Edição Revisada", livroAtualizado.getTitulo());
        assertEquals("Machado de Assis", livroAtualizado.getAutor());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com dados duplicados")
    void deveLancarExcecaoAoAtualizarComDadosDuplicados() {
        // Arrange
        Livro livro1 = new Livro();
        livro1.setTitulo("Dom Casmurro");
        livro1.setAutor("Machado de Assis");
        livroService.adicionar(livro1);

        Livro livro2 = new Livro();
        livro2.setTitulo("O Cortiço");
        livro2.setAutor("Aluísio Azevedo");
        Livro livroSalvo2 = livroService.adicionar(livro2);

        // Act & Assert
        livroSalvo2.setTitulo("Dom Casmurro");
        livroSalvo2.setAutor("Machado de Assis");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.atualizar(livroSalvo2));
        assertEquals("Já existe outro livro cadastrado com este título e autor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve limpar lista e resetar ID generator")
    void deveLimparListaEResetarIdGenerator() {
        // Arrange
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
        livroService.adicionar(livro);

        // Act
        livroService.limpar();

        // Assert
        assertEquals(0, livroService.listarTodos().size());

        // Verifica se o ID generator foi resetado
        Livro novoLivro = new Livro();
        novoLivro.setTitulo("O Cortiço");
        novoLivro.setAutor("Aluísio Azevedo");
        Livro livroSalvo = livroService.adicionar(novoLivro);
        assertEquals(1L, livroSalvo.getId());
    }
}
