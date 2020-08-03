package br.com.vinicius.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.vinicius.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	@Value("${security.jwt.expeiracao}")
	private String expiracao;

	@Value("${security.jwt.chave-assinatura}")
	private String chaveAssinatura;

	public String gerarToken(Usuario usuario) {
		long expString = Long.valueOf(expiracao);
		LocalDateTime dataHora = LocalDateTime.now().plusMinutes(expString);
		Date data = Date.from(dataHora.atZone(ZoneId.systemDefault()).toInstant());
		return Jwts.builder().setSubject(usuario.getLogin()).setExpiration(data)
				.signWith(SignatureAlgorithm.HS512, chaveAssinatura).compact();
	}

	public Claims obterClaims(String token) throws ExpiredJwtException {
		return Jwts.parser().setSigningKey(chaveAssinatura).parseClaimsJws(token).getBody();
	}

	public boolean tokenValido(String token) {
		try {
			Claims claims = obterClaims(token);
			Date dataExpiracacao = claims.getExpiration();
			LocalDateTime data = dataExpiracacao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			return !LocalDateTime.now().isAfter(data);
		} catch (Exception e) {
			return false;
		}
	}

	public String obterLogin(String token) throws ExpiredJwtException {
		return (String) obterClaims(token).getSubject();
	}
}
