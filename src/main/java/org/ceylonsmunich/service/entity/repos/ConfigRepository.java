package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Config;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfigRepository extends CrudRepository<Config,String> {

    @Override
    List<Config> findAll();
}
