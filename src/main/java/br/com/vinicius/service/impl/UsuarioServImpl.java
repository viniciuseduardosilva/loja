package br.com.vinicius.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.vinicius.exception.PasswordInvalidException;
import br.com.vinicius.model.Usuario;
import br.com.vinicius.repositories.UsuarioRepository;

@Service
public class UsuarioServImpl implements UserDetailsService{

	@Autowired
	private PasswordEncoder enconder;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Transactional
	public Usuario salvar(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public UserDetails autentica(Usuario usuario) {
		UserDetails user = loadUserByUsername(usuario.getLogin());
		boolean senhaCorreta = enconder.matches(usuario.getSenha()
												, user.getPassword());
		if(senhaCorreta) {
			return user;
		}
		throw new PasswordInvalidException();
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String email) 
			throws UsernameNotFoundException {
		
		Usuario user = usuarioRepository.findByLogin(email)
				.orElseThrow(() -> new UsernameNotFoundException("usuario nao encontrado"));
		
		
		
		return User
				.builder()
				.password(user.getSenha())
				.roles(user.getRoles())
				.username(user.getLogin())
				.build();
	}
	
	

}
