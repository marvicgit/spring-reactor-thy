package martin.site.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

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
	
	@Value("${ruta.subida}")
	private String rutaSubida;
	
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
				.doOnNext(p -> log.info("Cliente: " + p.getNombres()))
				.defaultIfEmpty(new Cliente());
		model.addAttribute("titulo", "Editar Plato");
		model.addAttribute("boton", "Editar");
		model.addAttribute("cliente", clienteMono);
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
	public Mono<String> operar(@Valid Cliente cliente, BindingResult validaciones, Model model,@RequestPart FilePart file, SessionStatus status) {
		if(validaciones.hasErrors()) {
			validaciones.reject("ERR780", "Error de Validacion de formulario");
			model.addAttribute("titulo", "errores de formulario de clientes");
			model.addAttribute("boton", "Guardar");
			return Mono.just("platos/form");
		} else {
			if(!file.filename().isEmpty()) {
				cliente.setUrlFoto(UUID.randomUUID().toString() + "-" + file.filename());
			}
			status.setComplete();
		return service.registrar(cliente)
				.doOnNext(c -> log.info("Cliente guardado: " + c.getNombres() + " Id: " + c.getId()))
				.flatMap(c -> {
					if(!file.filename().isEmpty()) {
						return file.transferTo(new File(rutaSubida + c.getUrlFoto()));
					}
					return Mono.empty();
				})
				.thenReturn("redirect:/clientes/listar?success=cliente+guardado");
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
	
	@GetMapping("/subidas/img/{nombreFoto:.+}")
	public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String nombreFoto) throws MalformedURLException {
		Path ruta = Paths.get(rutaSubida).resolve(nombreFoto).toAbsolutePath();
		
		Resource imagen = new UrlResource(ruta.toUri());
		
		return Mono.just(
				ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imagen.getFilename() + "\"")
				.body(imagen)
				);
	}
	
	@GetMapping("listar/datadriver")
	public Mono<String> listarDatadriver(Model model) {
		int cantidad_elementos_mostrar = 1;
		Flux<Cliente> clientes = service.listarDemorado();
		model.addAttribute("clientes", new ReactiveDataDriverContextVariable(clientes, cantidad_elementos_mostrar));
		model.addAttribute("titulo", "Listado de Clientes");
		return Mono.just("clientes/listar");
	}
	
	@GetMapping("listar/full")
	public Mono<String> listarFull(Model model) {
		Flux<Cliente> clientes = service.listarSobrecargado();
		model.addAttribute("clientes", clientes);
		model.addAttribute("titulo", "Listado de Clientes");
		return Mono.just("clientes/listar");
	}
	
	@GetMapping("listar/chunked")
	public Mono<String> listarChunked(Model model) {
		Flux<Cliente> clientes = service.listarSobrecargado();
		model.addAttribute("clientes", clientes);
		model.addAttribute("titulo", "Listado de Clientes");
		return Mono.just("clientes/listar-frag");
	}
}
