package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product,Long> {
    @Override
    List<Product> findAll();

    List<Product> findAllByOrderByIdAsc();

    Page<Product> findAll(Pageable pageable);
}
