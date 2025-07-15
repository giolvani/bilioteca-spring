package br.edu.ifpr.biblioteca_spring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
}
