package ch.zhaw.freelancer4u.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.freelancer4u.model.Company;

public interface CompanyRepository extends MongoRepository<Company, String> {
}
