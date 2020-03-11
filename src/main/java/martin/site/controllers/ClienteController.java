package martin.site.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import martin.site.documents.Cliente;
import martin.site.services.IClienteService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
	private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
	
	@Autowired
	private IClienteService service;
	
	@GetMapping("/listar")
	public Mono<String> listar(Model model) {
		
		Flux<Cliente> clientes = service.listar();
		
		clientes.doOnNext(p -> log.info(p.getNombres())).subscribe();
		
		model.addAttribute("clientes", clientes);
		model.addAttribute("titulo", "Listado de Clientes");
		return Mono.just("clientes/listar");
	}
	
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model model){
		Mono<Cliente> clienteMono = service.listarPorId(id)
				.doOnNext(p -> log.info("Plato: " + p.getNombres()))
				.defaultIfEmpty(new Cliente());
		model.addAttribute("titulo", "Editar Plato");
		model.addAttribute("boton", "Editar");
		model.addAttribute("plato", clienteMono);
		return Mono.just("clientes/form");
	}
	
	@GetMapping("/form")
	public Mono<String> crear(Model model) {
		model.addAttribute("cliente", new Cliente());
		model.addAttribute("titulo", "Formulario de cliente");
		model.addAttribute("boton", "Crear");
		
		return Mono.just("clientes/form");
	}
	
	@PostMapping("/operar")
	public Mono<String> operar(@Valid Cliente cliente, BindingResult validaciones, Model model, SessionStatus status) {
		if(validaciones.hasErrors()) {
			validaciones.reject("ERR780", "Error de Validacion de formulario");
			model.addAttribute("titulo", "errores de formulario de clientes");
			model.addAttribute("boton", "Guardar");
			return Mono.just("platos/form");
		} else {
			status.setComplete();
		return service.registrar(cliente)
				.doOnNext(p -> log.info("Plato guardado: " + p.getNombres() + " Id: " + p.getId()))
				.thenReturn("redirect:/clientes/listar");
		}			
	}
	
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id, Model model){
		return service.listarPorId(id)
				.defaultIfEmpty(new Cliente())
				.flatMap(p -> {
					if(p.getId() == null) {
						return Mono.error(new InterruptedException("No existe Id"));
					}
					return Mono.just(p);
				}).flatMap(p -> {
					log.info("a punto de eliminar el cliente: " + p.getNombres());
					return service.eliminar(p.getId());
				}).then(Mono.just("redirect:/clientes/listar"))
				.onErrorResume(ex -> Mono.just("redirect:/clientes/listar?error=500"));
	}
}
