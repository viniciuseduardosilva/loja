package br.com.vinicius.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import br.com.vinicius.model.Email;
import br.com.vinicius.model.Senha;
import br.com.vinicius.model.Usuario;
import br.com.vinicius.model.dto.ClienteDTO;
import br.com.vinicius.model.dto.CredenciaisDTO;
import br.com.vinicius.model.dto.TokenDTO;
import br.com.vinicius.model.response.MessageResponse;
import br.com.vinicius.repositories.UsuarioRepository;
import br.com.vinicius.service.ClienteDtoService;
import br.com.vinicius.service.ClienteService;
import br.com.vinicius.service.JwtService;
import br.com.vinicius.service.impl.UsuarioServImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("api/usuario")
@Api("usuario")
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
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Cadastrar cliente com confirção via email")
	@ApiResponses({
		@ApiResponse(code = 201, message = "cliente encontrado"),
		@ApiResponse (code = 404, message = "cliente nao encontrado")
	})
	public ResponseEntity<MessageResponse> cadastra(@RequestBody
													@ApiParam ("recebimento do formulario para cadastro do cliente") 
													ClienteDTO clienteDTO) {
		if (clienteService.buscaEmail(clienteDTO.getEmail())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario já cadastrado!");
		}
		String senha = encoder.encode(clienteDTO.getSenha());
		clienteDTO.setSenha(senha);
		clienteDtoService.salvaClienteDto(Usuario.builder()
				.login(clienteDTO.getEmail())
				.senha(senha)
				.roles("")
				.build(),
				Cliente.builder()
					.email(clienteDTO.getEmail())
					.cadastro(LocalDate.now())
					.telefone(clienteDTO.getTelefone())
					.nome(clienteDTO.getNome())
					.build());
		return new ResponseEntity<MessageResponse>(new MessageResponse("Email Enviado com Sucesso!!")
				, HttpStatus.OK);
	}

	@GetMapping("/confirmacao/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("URL do email para finalizar cadastro")
	public ResponseEntity<MessageResponse> confirmacao(@PathVariable 
									@ApiParam ("token enviado por email") String token) {
		boolean isValid = jwtService.tokenValido(token);
		if (isValid) {
			String login = jwtService.obterLogin(token);
			clienteDtoService.validaUsuario(login);
			return new ResponseEntity<MessageResponse>(new MessageResponse("Cadastrado com Sucesso!!")
					, HttpStatus.CREATED);
		}
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cliente não cadasrtrado!");
	}

	@PostMapping("/recuperar-senha")
	@ApiOperation("Solicitação via email para alterar a senha")
	public ResponseEntity<MessageResponse> recuperarSenha(@RequestBody
			@ApiParam ("Email cadastrado") Email email) {
		if (usuarioRepository.existsByLogin(email.getEmail())) {
			clienteDtoService.recuperaSenha(email.getEmail());
			return new ResponseEntity<MessageResponse>(new MessageResponse("Verifique seu email!!")
					, HttpStatus.CREATED);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não cadasrtrado!");
	}
	
	@GetMapping("/nova-senha/{token}")
	@ApiOperation("URL com o token para alteração de senha")
	public ResponseEntity<MessageResponse> novaSenha(@PathVariable 
			@ApiParam ("Token de autorizaçao de alteraçao de senha") String token,
			@ApiParam ("Nova senha enviada pelo corpo da Requisição") @RequestBody Senha senha) {
		boolean isValid = jwtService.tokenValido(token);
		if (isValid) {
			String email = jwtService.obterLogin(token);
			clienteDtoService.alteraSenha(email, senha.getSenha());
			return new ResponseEntity<MessageResponse>(new MessageResponse("Senha alterada com sucesso!")
					, HttpStatus.CREATED);
		}
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cliente não cadasrtrado!");
	}
	
	@PostMapping("/auth")
	@ApiOperation("Autenticador da contam via token")
	public TokenDTO autentica(@RequestBody
			@ApiParam ("Credenciais para login do cliente") CredenciaisDTO credenciais) {
		try {
			Usuario usuario = Usuario.builder()
								.login(credenciais.getLogin())
								.senha(credenciais.getSenha())
								.build();
			UserDetails usuarioAutenticado = impl.autentica(usuario);
			String token = jwtService.gerarToken(usuario);
			System.out.println(usuario.toString());
			return new TokenDTO(usuario.getLogin(), token);
		} catch (UsernameNotFoundException | PasswordInvalidException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}
	}
	
//	@PostMapping("sava")
//	public Usuario salva(@RequestBody Usuario usuario) {
//		String senha = encoder.encode(usuario.getSenha());
//		usuario.setSenha(senha);
//		return usuarioRepository.save(usuario);
//	 }
//	
//	@GetMapping("/todos")
//	public List<Usuario> trastodos() {
//		return usuarioRepository.findAll();
//	}
	
	
	}
