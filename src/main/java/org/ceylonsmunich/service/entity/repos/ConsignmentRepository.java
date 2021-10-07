package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Consignment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConsignmentRepository extends CrudRepository<Consignment,Long> {

    @Override
    List<Consignment> findAll();
    List<Consignment> findByOrderById();
}
