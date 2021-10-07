package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency,String> {
    @Override
    List<Currency> findAll();


}
