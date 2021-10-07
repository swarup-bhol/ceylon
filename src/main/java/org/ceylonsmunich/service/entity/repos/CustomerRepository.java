package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer,Long> {
    @Override
    List<Customer> findAll();
    List<Customer> findAllByOrderByIdAsc();
    List<Customer> findAllByCompany(String company);
}
