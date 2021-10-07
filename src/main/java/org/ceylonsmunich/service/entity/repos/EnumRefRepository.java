package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.EnumRef;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnumRefRepository extends CrudRepository<EnumRef,String> {
    @Override
    List<EnumRef> findAll();
}
