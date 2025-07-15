package br.edu.ifpr.biblioteca_spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.edu.ifpr.biblioteca_spring.models.Livro;
import br.edu.ifpr.biblioteca_spring.models.Usuario;
import br.edu.ifpr.biblioteca_spring.service.EmprestimoService;
import br.edu.ifpr.biblioteca_spring.service.LivroService;
import br.edu.ifpr.biblioteca_spring.service.UsuariosService;

@SpringBootApplication
public class BibliotecaSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaSpringApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(LivroService livroService, UsuariosService usuarioService, EmprestimoService emprestimoService) {
		return args -> {

			livroService.adicionar(new Livro(null, "O Cortiço", "Aluísio Azevedo"));
			livroService.adicionar(new Livro(null, "Dom Casmurro", "Machado de Assis"));
			livroService.adicionar(new Livro(null, "O Guarani", "José de Alencar"));
			livroService.adicionar(new Livro(null, "Capitães da Areia", "Jorge Amado"));
			livroService.adicionar(new Livro(null, "A Moreninha", "Joaquim Manuel de Macedo"));
			livroService.adicionar(new Livro(null, "Iracema", "José de Alencar"));

			usuarioService.adicionar(new Usuario(null, "Maria Silva Santos", "12345678901"));
			usuarioService.adicionar(new Usuario(null, "João Pedro Oliveira", "98765432100"));
			usuarioService.adicionar(new Usuario(null, "Ana Carolina Ferreira", "11122233344"));
			usuarioService.adicionar(new Usuario(null, "Carlos Eduardo Lima", "55566677788"));

			emprestimoService.emprestarLivro(usuarioService.buscarPorId(1L).get(), livroService.buscarPorId(1L).get());
			emprestimoService.emprestarLivro(usuarioService.buscarPorId(2L).get(), livroService.buscarPorId(2L).get());
		};
	}

}
