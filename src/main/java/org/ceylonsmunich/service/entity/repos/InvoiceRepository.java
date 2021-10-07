package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.Invoice;
import org.ceylonsmunich.service.entity.enums.InvoiceState;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice,Long> {
    @Override
    List<Invoice> findAll();

    List<Invoice> findByOrderById();


}
