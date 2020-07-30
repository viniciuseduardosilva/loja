package br.com.vinicius.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import br.com.vinicius.model.Cliente;

@Component
public class ClienteCriteria {
	
	@PersistenceContext
	private EntityManager manager;

	public List<Cliente> consulta(LocalDate dataInicial, LocalDate dataFinal) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Cliente> query = criteriaBuilder.createQuery(Cliente.class);
		Root<Cliente> root = query.from(Cliente.class);
//		Path<String> placaPath = root.get("veiculo").get("veiculoId").get("placa");
		Path<LocalDate> dataCadastroPath = root.<LocalDate>get("dtcad");
		List<Predicate> predicates = new ArrayList<Predicate>();
//		if (!placa.equals("")) {
//			Predicate frotaIgual = criteriaBuilder.equal(placaPath, placa);
//			predicates.add(frotaIgual);
//		}
		if (dataInicial != null && dataFinal != null) {
			Predicate dataIgual = criteriaBuilder.between(dataCadastroPath, dataInicial, dataFinal);
			predicates.add(dataIgual);
		}
		query.where((Predicate[]) predicates.toArray(new Predicate[0]));
		TypedQuery<Cliente> typedQuery = manager.createQuery(query);
		return typedQuery.getResultList();
	}

}
