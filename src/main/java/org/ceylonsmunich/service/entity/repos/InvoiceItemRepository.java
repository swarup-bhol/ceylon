package org.ceylonsmunich.service.entity.repos;

import org.ceylonsmunich.service.entity.InvoicedItem;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceItemRepository extends CrudRepository<InvoicedItem, Long> {
}
