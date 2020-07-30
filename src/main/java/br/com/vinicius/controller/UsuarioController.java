package br.com.vinicius.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.vinicius.exception.PasswordInvalidException;
import br.com.vinicius.model.Cliente;
import br.com.vinicius.model.Usuario;
import br.com.vinicius.model.dto.ClienteDTO;
import br.com.vinicius.model.dto.CredenciaisDTO;
import br.com.vinicius.model.dto.TokenDTO;
import br.com.vinicius.service.ClienteDtoService;
import br.com.vinicius.service.ClienteService;
import br.com.vinicius.service.JwtService;
import br.com.vinicius.service.impl.UsuarioServImpl;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioServImpl impl;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ClienteDtoService clienteDtoService;
	

//	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)
//	public Usuario salvar(@RequestBody Usuario usuario) {
//		
//		String senha = encoder.encode(usuario.getSenha());
//		usuario.setSenha(senha);
//		return impl.salvar(usuario);
//	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String cadastra(@RequestBody ClienteDTO clienteDTO) {
		if (clienteService.buscaEmail(clienteDTO.getEmail())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario já cadastrado!");
		}
		String senha = encoder.encode(clienteDTO.getSenha());
		clienteDTO.setSenha(senha);
		clienteDtoService.salvaClienteDto(
				Usuario.builder()
					.login(clienteDTO.getEmail())
					.senha(senha)
					.roles("")
					.build() ,
				Cliente.builder()
						.email(clienteDTO.getEmail())
						.cadastro(LocalDate.now())
						.telefone(clienteDTO.getTelefone())
						.nome(clienteDTO.getNome())
						.build());
		return "email enviado";
	}

	@GetMapping("/confirmacao/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public String confirmacao(@PathVariable String token) {
		boolean isValid = jwtService.tokenValido(token);
		if (isValid) {
			String login = jwtService.obterLogin(token);
			Usuario usuario = clienteDtoService.validaUsuario(login);
			return usuario.getRoles();
		}
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cliente não cadasrtrado!");
		
	}
	
	@GetMapping("/nova-senha/{token}")
	public String novaSenha(@PathVariable String token, @RequestBody String senha) {
		boolean isValid = jwtService.tokenValido(token);
		if (isValid) {
			String email = jwtService.obterLogin(token);
			clienteDtoService.alteraSenha(email, senha);
			return "Senha alterada com sucesso!";
		}
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cliente não cadasrtrado!");
	}
	
	@PostMapping("/recuperar-senha")
	public String recuperarSenha(@RequestBody String email) {
		if(clienteService.buscaEmail(email)) {
			clienteDtoService.recuperaSenha(email);
			return "verifique o email";
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não cadasrtrado!");
	}
	
	@PostMapping("/auth")
	public TokenDTO autentica(@RequestBody CredenciaisDTO credenciais) {
		try {
			Usuario usuario = Usuario.builder().login(credenciais.getLogin()).senha(credenciais.getSenha()).build();
			UserDetails usuarioAutenticado = impl.autentica(usuario);
			String token = jwtService.gerarToken(usuario);
			return new TokenDTO(usuario.getLogin(), token);
		} catch (UsernameNotFoundException | PasswordInvalidException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}
	}

}
