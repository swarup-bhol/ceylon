package org.ceylonsmunich.service.config.table;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeTableRepository extends JpaRepository<FeTable,String> {
	Optional<FeTable> findByTableId(String tableId);
}
