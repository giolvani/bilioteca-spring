package br.edu.ifpr.biblioteca_spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpr.biblioteca_spring.models.Emprestimo;
import br.edu.ifpr.biblioteca_spring.models.Livro;
import br.edu.ifpr.biblioteca_spring.models.Usuario;
import br.edu.ifpr.biblioteca_spring.service.EmprestimoService;
import br.edu.ifpr.biblioteca_spring.service.LivroService;
import br.edu.ifpr.biblioteca_spring.service.UsuariosService;

@WebMvcTest(EmprestimoController.class)
@ActiveProfiles("test")
public class EmprestimoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmprestimoService emprestimoService;

    @MockBean
    private LivroService livroService;

    @MockBean
    private UsuariosService usuariosService;

    @Test
    public void deveRetornarAViewDeListagemDeEmprestimos() throws Exception {
        // Mock da lista de empréstimos
        List<Emprestimo> emprestimos = Arrays.asList(
                new Emprestimo(1L, new Usuario(1L, "João Silva", "12345678901"),
                        new Livro(1L, "Dom Casmurro", "Machado de Assis"),
                        LocalDate.now(), LocalDate.now().plusDays(7)));
        when(emprestimoService.listarTodos()).thenReturn(emprestimos);

        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(view().name("emprestimos/lista"))
                .andExpect(model().attributeExists("emprestimos"));
    }

    @Test
    public void deveRetornarAViewDoFormularioDeEmprestimo() throws Exception {
        // Mock das listas de usuários e livros
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(1L, "João Silva", "12345678901"),
                new Usuario(2L, "Maria Santos", "98765432100"));
        List<Livro> livros = Arrays.asList(
                new Livro(1L, "Dom Casmurro", "Machado de Assis"),
                new Livro(2L, "O Cortiço", "Aluísio Azevedo"));

        when(usuariosService.listarTodos()).thenReturn(usuarios);
        when(livroService.listarTodos()).thenReturn(livros);

        mockMvc.perform(get("/emprestimos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("emprestimos/form"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attributeExists("livros"));
    }

    @Test
    public void deveRealizarEmprestimoComSucesso() throws Exception {
        // Mock dos objetos necessários
        Usuario usuario = new Usuario(1L, "João Silva", "12345678901");
        Livro livro = new Livro(1L, "Dom Casmurro", "Machado de Assis");

        when(usuariosService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(livroService.buscarPorId(1L)).thenReturn(Optional.of(livro));
        when(emprestimoService.emprestarLivro(any(Usuario.class), any(Livro.class)))
                .thenReturn(new Emprestimo(1L, usuario, livro, LocalDate.now(), LocalDate.now().plusDays(7)));

        mockMvc.perform(post("/emprestimos")
                .param("usuarioId", "1")
                .param("livroId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/emprestimos"))
                .andExpect(flash().attribute("sucesso", "Empréstimo realizado com sucesso!"));
    }

    @Test
    public void deveRetornarErroQuandoParametrosForemNulos() throws Exception {
        // Mock das listas para retornar ao formulário
        when(usuariosService.listarTodos()).thenReturn(Arrays.asList());
        when(livroService.listarTodos()).thenReturn(Arrays.asList());

        mockMvc.perform(post("/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(view().name("emprestimos/form"))
                .andExpect(model().attribute("erro", "Usuário e livro devem ser selecionados."));
    }

    @Test
    public void deveRetornarErroQuandoUsuarioNaoForEncontrado() throws Exception {
        // Mock: usuário não encontrado, livro encontrado
        when(usuariosService.buscarPorId(1L)).thenReturn(Optional.empty());
        when(livroService.buscarPorId(1L)).thenReturn(Optional.of(new Livro(1L, "Dom Casmurro", "Machado de Assis")));
        when(usuariosService.listarTodos()).thenReturn(Arrays.asList());
        when(livroService.listarTodos()).thenReturn(Arrays.asList());

        mockMvc.perform(post("/emprestimos")
                .param("usuarioId", "1")
                .param("livroId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("emprestimos/form"))
                .andExpect(model().attribute("erro", "Erro: Usuário não encontrado. "));
    }

    @Test
    public void deveRetornarErroQuandoLivroNaoForEncontrado() throws Exception {
        // Mock: usuário encontrado, livro não encontrado
        when(usuariosService.buscarPorId(1L)).thenReturn(Optional.of(new Usuario(1L, "João Silva", "12345678901")));
        when(livroService.buscarPorId(1L)).thenReturn(Optional.empty());
        when(usuariosService.listarTodos()).thenReturn(Arrays.asList());
        when(livroService.listarTodos()).thenReturn(Arrays.asList());

        mockMvc.perform(post("/emprestimos")
                .param("usuarioId", "1")
                .param("livroId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("emprestimos/form"))
                .andExpect(model().attribute("erro", "Erro: Livro não encontrado."));
    }

    @Test
    public void deveRetornarErroQuandoEmprestimoFalhar() throws Exception {
        // Mock dos objetos necessários
        Usuario usuario = new Usuario(1L, "João Silva", "12345678901");
        Livro livro = new Livro(1L, "Dom Casmurro", "Machado de Assis");

        when(usuariosService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(livroService.buscarPorId(1L)).thenReturn(Optional.of(livro));
        when(emprestimoService.emprestarLivro(any(Usuario.class), any(Livro.class)))
                .thenThrow(new IllegalArgumentException("Usuário já possui 3 livros emprestados."));
        when(usuariosService.listarTodos()).thenReturn(Arrays.asList());
        when(livroService.listarTodos()).thenReturn(Arrays.asList());

        mockMvc.perform(post("/emprestimos")
                .param("usuarioId", "1")
                .param("livroId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("emprestimos/form"))
                .andExpect(model().attribute("erro", "Usuário já possui 3 livros emprestados."));
    }

    @Test
    public void deveDevolverLivroComSucesso() throws Exception {
        when(emprestimoService.devolverLivro(1L))
                .thenReturn(Optional.of("Livro devolvido com sucesso!"));

        mockMvc.perform(get("/emprestimos/devolucao")
                .param("emprestimoId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/emprestimos"))
                .andExpect(flash().attribute("sucesso", "Livro devolvido com sucesso!"));
    }

    @Test
    public void deveRetornarErroQuandoDevolucaoFalhar() throws Exception {
        when(emprestimoService.devolverLivro(1L))
                .thenReturn(Optional.of("Empréstimo não encontrado."));

        mockMvc.perform(get("/emprestimos/devolucao")
                .param("emprestimoId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/emprestimos"))
                .andExpect(flash().attribute("erro", "Empréstimo não encontrado."));
    }

    @Test
    public void deveRetornarErroQuandoEmprestimoIdForNulo() throws Exception {
        mockMvc.perform(get("/emprestimos/devolucao"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/emprestimos"))
                .andExpect(flash().attribute("erro", "ID do empréstimo é obrigatório."));
    }
}
