package martin.site.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import martin.site.documents.Cliente;

public interface IClienteRepo extends ReactiveMongoRepository<Cliente, String> {

}
