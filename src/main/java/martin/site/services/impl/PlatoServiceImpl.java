package martin.site.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import martin.site.documents.Plato;
import martin.site.repo.IPlatoRepo;
import martin.site.services.IPlatoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlatoServiceImpl implements IPlatoService{

	@Autowired
	private IPlatoRepo repo;
	
	@Override
	public Mono<Plato> registrar(Plato t) {
		return repo.save(t);
	}

	@Override
	public Mono<Plato> modificar(Plato t) {
		return repo.save(t);
	}

	@Override
	public Flux<Plato> listar() {
		return repo.findAll();
	}

	@Override
	public Mono<Plato> listarPorId(String v) {
		return repo.findById(v);
	}

	@Override
	public Mono<Void> eliminar(String v) {
		return repo.deleteById(v);
	}

}