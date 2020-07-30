package br.com.vinicius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.vinicius.model.Cliente;
import br.com.vinicius.model.Usuario;
import br.com.vinicius.repositories.ClienteRepository;
import br.com.vinicius.repositories.UsuarioRepository;

@Service
public class ClienteDtoService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder enconder;

	public void recuperaSenha(String email) {
		Usuario usuario = usuarioRepository.findByLogin(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
		String token = jwtService.gerarToken(usuario);
		emailService.sendEmail(email, token);
	}

	public void salvaClienteDto(Usuario usuario, Cliente cliente) {
		usuarioRepository.save(usuario);
		clienteRepository.save(cliente);
		String token = jwtService.gerarToken(usuario);
		emailService.sendEmail(cliente.getEmail(), token);
	}

	public Usuario validaUsuario(String login) {
		return usuarioRepository.findByLogin(login).map(usuario -> {
			usuario.setRoles("USER");
			return usuarioRepository.save(usuario);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}

	public void alteraSenha(String email, String senha) {
		Usuario usuario = usuarioRepository.findByLogin(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
		String novaSenha = enconder.encode(senha);
		usuario.setSenha(novaSenha);
	}
}
