package br.edu.ifpr.biblioteca_spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpr.biblioteca_spring.models.Usuario;
import br.edu.ifpr.biblioteca_spring.service.UsuariosService;

@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuariosService usuariosService;

    @Test
    public void deveRetornarAViewDeListagemDeUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"));
    }

    @Test
    public void deveRetornarAViewDeCadastroDeUsuario() throws Exception {
        mockMvc.perform(get("/usuarios/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"));
    }

    @Test
    public void deveAdicionarPessoaCorretamente() throws Exception {
        // Mock do serviço para retornar um usuário
        Usuario usuarioMock = new Usuario(1L, "João Silva", "12345678901");
        when(usuariosService.adicionar(any(Usuario.class))).thenReturn(usuarioMock);

        mockMvc.perform(post("/usuarios")
                .param("nome", "João Silva")
                .param("cpf", "12345678901"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attribute("sucesso", "Usuário cadastrado com sucesso!"));
    }

    @Test
    public void deveVoltarParaOFormularioEmCasoDeErroDeValidacao() throws Exception {
        mockMvc.perform(post("/usuarios")
                .param("nome", "") // nome inválido
                .param("cpf", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"));
    }

    @Test
    public void deveVoltarParaOFormularioComCpfInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                .param("nome", "João Silva")
                .param("cpf", "123")) // CPF inválido
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"));
    }

    @Test
    public void deveVoltarParaOFormularioComCpfInvalidoEModeloComErros() throws Exception {
        mockMvc.perform(post("/usuarios")
                .param("nome", "João Silva")
                .param("cpf", "123.456.789-99") // CPF inválido
                .param("email", "joao@teste.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void deveRetornarErroQuandoCpfJaExistir() throws Exception {
        // Mock do serviço para lançar exceção de CPF duplicado
        when(usuariosService.adicionar(any(Usuario.class)))
            .thenThrow(new IllegalArgumentException("Já existe um usuário cadastrado com este CPF."));

        mockMvc.perform(post("/usuarios")
                .param("nome", "João Silva")
                .param("cpf", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attribute("erro", "Já existe um usuário cadastrado com este CPF."));
    }

    @Test
    public void deveRetornarErroQuandoUsuarioForNulo() throws Exception {
        // Mock do serviço para lançar exceção de usuário nulo
        when(usuariosService.adicionar(any(Usuario.class)))
            .thenThrow(new IllegalArgumentException("Usuário não pode ser nulo."));

        mockMvc.perform(post("/usuarios")
                .param("nome", "João Silva")
                .param("cpf", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/form"))
                .andExpect(model().attribute("erro", "Usuário não pode ser nulo."));
    }
}
