package martin.site.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import martin.site.documents.Factura;
import martin.site.pagination.PageSupport;
import martin.site.repo.IFacturaRepo;
import martin.site.services.IFacturaService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FacturaServiceImpl implements IFacturaService {

	@Autowired
	private IFacturaRepo repo;
	
	@Override
	public Mono<Factura> registrar(Factura t) {
		return repo.save(t);
	}

	@Override
	public Mono<Factura> modificar(Factura t) {
		return repo.save(t);
	}

	@Override
	public Flux<Factura> listar() {
		return repo.findAll();
	}

	@Override
	public Mono<Factura> listarPorId(String v) {
		return repo.findById(v);
	}

	@Override
	public Mono<Void> eliminar(String v) {
		return repo.deleteById(v);
	}

	@Override
	public Mono<PageSupport<Factura>> listarPagina(Pageable Page) {
		// TODO Auto-generated method stub
		return null;
	}

}
