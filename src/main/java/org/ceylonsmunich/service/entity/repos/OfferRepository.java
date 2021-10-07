package org.ceylonsmunich.service.entity.repos;


import org.ceylonsmunich.service.entity.Offer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OfferRepository extends CrudRepository<Offer,Long> {

    @Override
    List<Offer> findAll();
    List<Offer> findByOrderById();
}
