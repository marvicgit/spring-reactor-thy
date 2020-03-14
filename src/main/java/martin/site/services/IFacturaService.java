package martin.site.services;

import org.springframework.data.domain.Pageable;

import martin.site.documents.Factura;
import martin.site.pagination.PageSupport;
import reactor.core.publisher.Mono;

public interface IFacturaService extends ICRUD<Factura, String>{

	Mono<PageSupport<Factura>> listarPagina(Pageable Page);
}
