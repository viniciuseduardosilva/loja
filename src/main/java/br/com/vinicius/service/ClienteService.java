package br.com.vinicius.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.vinicius.model.Cliente;
import br.com.vinicius.repositories.ClienteCriteria;
import br.com.vinicius.repositories.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteCriteria criteria;
	
	@Autowired
	private ClienteRepository repository;
	
	public List<Cliente> consulta(String dataInicial, String dataFinal) {
		LocalDate dataIni = LocalDate.now();
		LocalDate dataFin = LocalDate.now();
		if (!dataInicial.equals("") && !dataInicial.equals("")) {
			dataIni = LocalDate.parse(dataInicial);
			dataFin = LocalDate.parse(dataFinal);
		} else {
			dataIni = null;
			dataFin = null;
		}
		return criteria.consulta(dataIni, dataFin);
	}

	public boolean buscaEmail(String email) {
		return repository.existsByEmail(email);
	}
}
