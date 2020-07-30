package br.com.vinicius.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.vinicius.model.Cliente;
import br.com.vinicius.repositories.ClienteRepository;


@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "*")
public class ClienteController {
	

	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@GetMapping("{email}")
	public Cliente cliente(@PathVariable String email) {
		return clienteRepository.findByEmail(email)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}
	
	@GetMapping
	public List<Cliente> findAllClientes(Cliente filtro){	
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING );
       Example example = Example.of(filtro, matcher);
		return clienteRepository.findAll(example);
	}
	
	@GetMapping("teste")
	public String teste() {
		return "teste";
	}
	
	@DeleteMapping("{email}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById( @PathVariable String email ) {
		clienteRepository.findByEmail(email)
        .map( cliente -> {
        	clienteRepository.delete(cliente );
            return cliente;
        })
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Cliente não encontrado") );
	}
	
  @PutMapping("{email}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update( @PathVariable  String email,
                      @RequestBody Cliente cliente ){
	  clienteRepository
              .findByEmail(email)
              .map( clienteExistente -> {
                  cliente.setEmail(clienteExistente.getEmail());
                  clienteRepository.save(cliente);
                  return clienteExistente;
              }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                  "Cliente não encontrado") );
  }
  
  

	
//	@Autowired
//	private ClienteRepository repository;
//	
//	@GetMapping("{id}")
//	public Cliente getClienteByID(@PathVariable Long id) {
//		return repository.findById(id)
//				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
//	}
//	
//	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)
//    public Cliente save( @RequestBody Cliente cliente ){
//
//		cliente.setCadastro(LocalDate.now());
//        return repository.save(cliente);
//    }
//	
//	@GetMapping
//	public List<Cliente> findAllClientes(Cliente filtro){
//		
//        ExampleMatcher matcher = ExampleMatcher
//                .matching()
//                .withIgnoreCase()
//                .withStringMatcher(
//                        ExampleMatcher.StringMatcher.CONTAINING );
//       Example example = Example.of(filtro, matcher);
//		return repository.findAll(example);
//	}
//	
//	@DeleteMapping("{id}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void deleteById( @PathVariable Long id ) {
//        repository.findById(id)
//        .map( cliente -> {
//            repository.delete(cliente );
//            return cliente;
//        })
//        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                "Cliente não encontrado") );
//	}
//	
//    @PutMapping("{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update( @PathVariable Long id,
//                        @RequestBody Cliente cliente ){
//    	repository
//                .findById(id)
//                .map( clienteExistente -> {
//                    cliente.setId(clienteExistente.getId());
//                    repository.save(cliente);
//                    return clienteExistente;
//                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                    "Cliente não encontrado") );
//    }
//	
	
}
