package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.ProductOverview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductOverviewRepository extends CrudRepository<ProductOverview, Long> {

    List<ProductOverview> findAllByProductId(Long id);
}
