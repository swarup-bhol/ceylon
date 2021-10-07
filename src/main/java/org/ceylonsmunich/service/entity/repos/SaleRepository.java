package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Sale;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SaleRepository extends CrudRepository<Sale,Long> {

    @Override
    List<Sale> findAll();

    List<Sale> findAllByOrderByIdAsc();
}
