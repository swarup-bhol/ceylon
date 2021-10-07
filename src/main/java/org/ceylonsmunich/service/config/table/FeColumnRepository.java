package org.ceylonsmunich.service.config.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeColumnRepository extends JpaRepository<FeColumn,Long> {


}
