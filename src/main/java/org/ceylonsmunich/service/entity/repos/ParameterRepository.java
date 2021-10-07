package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Parameters;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

public interface ParameterRepository extends CrudRepository<Parameters,String> {

    @Override
    List<Parameters> findAll();

    Optional<Parameters> findByName(String name);
}
