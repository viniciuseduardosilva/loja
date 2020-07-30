package br.com.vinicius.exception;

public class PasswordInvalidException extends RuntimeException {

	public PasswordInvalidException() {
		super("senha invalida");
	}
	
}
