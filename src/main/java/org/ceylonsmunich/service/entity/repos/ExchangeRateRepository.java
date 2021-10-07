package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.ExchangeRate;
import org.ceylonsmunich.service.entity.id.ExchangeRateId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, ExchangeRateId> {
    @Override
    List<ExchangeRate> findAll();
}
