package martin.site.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import martin.site.documents.Plato;

public interface IPlatoRepo extends ReactiveMongoRepository<Plato, String> {

}
