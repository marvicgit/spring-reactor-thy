package martin.site.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import martin.site.documents.Cliente;
import martin.site.repo.IClienteRepo;
import martin.site.services.IClienteService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteRepo repo;
	
	@Override
	public Mono<Cliente> registrar(Cliente t) {
		return repo.save(t);
	}

	@Override
	public Mono<Cliente> modificar(Cliente t) {
		return repo.save(t);
	}

	@Override
	public Flux<Cliente> listar() {
		return repo.findAll();
	}

	@Override
	public Mono<Cliente> listarPorId(String v) {
		return repo.findById(v);
	}

	@Override
	public Mono<Void> eliminar(String v) {
		return repo.deleteById(v);
	}

}
