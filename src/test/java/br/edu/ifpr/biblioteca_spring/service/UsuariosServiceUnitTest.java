package br.edu.ifpr.biblioteca_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.edu.ifpr.biblioteca_spring.models.Usuario;

@SpringBootTest
public class UsuariosServiceUnitTest {

    @Autowired
    private UsuariosService usuariosService;

    @BeforeEach
    public void setUp() {
        // Limpa a lista antes de cada teste para evitar interferência
        usuariosService.limpar();
    }

    @Test
    public void deveAdicionarUsuarioComSucesso() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");

        // Act
        Usuario resultado = usuariosService.adicionar(usuario);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertEquals("João Silva", resultado.getNome());
        assertEquals("12345678901", resultado.getCpf());
    }

    @Test
    public void deveRetornarUsuarioParaIdExistente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNome("Maria Santos");
        usuario.setCpf("98765432109");

        Usuario usuarioSalvo = usuariosService.adicionar(usuario);

        // Act
        Usuario encontrado = usuariosService.buscarPorId(usuarioSalvo.getId()).get();

        // Assert
        assertNotNull(encontrado);
        assertEquals(usuarioSalvo.getId(), encontrado.getId());
        assertEquals("Maria Santos", encontrado.getNome());
    }

    @Test
    public void deveLancarExcecaoAoBuscarPorIdInexistente() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuariosService.buscarPorId(999L);
        });

        assertEquals("Usuario inexistente.", exception.getMessage());
    }

    @Test
    public void deveRetornarListaDosUsuariosCadastrados() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Ana Costa");
        usuario1.setCpf("11111111111");

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Carlos Lima");
        usuario2.setCpf("22222222222");

        // Act
        usuariosService.adicionar(usuario1);
        usuariosService.adicionar(usuario2);

        List<Usuario> usuarios = usuariosService.listarTodos();

        // Assert
        assertThat(usuarios).hasSize(2);
        assertTrue(usuarios.stream().anyMatch(u -> u.getNome().equals("Ana Costa")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getNome().equals("Carlos Lima")));
    }

    @Test
    public void deveGerarIdsSequenciais() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Primeiro");
        usuario1.setCpf("11111111111");

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Segundo");
        usuario2.setCpf("22222222222");

        // Act
        Usuario resultado1 = usuariosService.adicionar(usuario1);
        Usuario resultado2 = usuariosService.adicionar(usuario2);

        // Assert
        assertThat(resultado1.getId()).isLessThan(resultado2.getId());
        assertThat(resultado2.getId() - resultado1.getId()).isEqualTo(1L);
    }
}
