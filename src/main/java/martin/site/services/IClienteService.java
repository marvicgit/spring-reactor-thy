package martin.site.services;

import martin.site.documents.Cliente;
import reactor.core.publisher.Flux;

public interface IClienteService extends ICRUD<Cliente, String>{

	Flux<Cliente> listarDemorado();
	Flux<Cliente> listarSobrecargado();
}
