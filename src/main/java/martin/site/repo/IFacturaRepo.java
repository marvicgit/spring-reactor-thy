package martin.site.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import martin.site.documents.Factura;

public interface IFacturaRepo extends ReactiveMongoRepository<Factura, String> {

}
