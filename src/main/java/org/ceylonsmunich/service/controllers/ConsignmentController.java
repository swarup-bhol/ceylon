package org.ceylonsmunich.service.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.ceylonsmunich.service.entity.Consignment;
import org.ceylonsmunich.service.entity.Invoice;
import org.ceylonsmunich.service.entity.InvoicedItem;
import org.ceylonsmunich.service.entity.Parameters;
import org.ceylonsmunich.service.entity.repos.ConsignmentRepository;
import org.ceylonsmunich.service.entity.repos.ConstantRepository;
import org.ceylonsmunich.service.entity.repos.InvoiceItemRepository;
import org.ceylonsmunich.service.entity.repos.ParameterRepository;
import org.ceylonsmunich.service.serializers.ConsignmentView;
import org.ceylonsmunich.service.serializers.InvoiceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ConsignmentController {
    @Autowired
    private ConsignmentRepository consignmentRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @JsonView(ConsignmentView.DefaultOnly.class)
    @RequestMapping(value = "/api/consignment", method = RequestMethod.GET)
    public List<Consignment> getAll(){
        return consignmentRepository.findByOrderById();
    }

    @JsonView(ConsignmentView.Details.class)
    @RequestMapping(value = "/api/consignment/{id}")
    public Consignment find(@PathVariable Long id){
        return consignmentRepository.findById(id).orElse(null);
    }

    @JsonView(ConsignmentView.Details.class)
    @RequestMapping(value = "/api/consignment/update")
    public Consignment save(@RequestBody Consignment invoice){
        List<InvoicedItem> items = new ArrayList<>();
        for(InvoicedItem item: invoice.getItems()){
            items.add(invoiceItemRepository.save(item));
        }
        invoice.setItems(items);
        if(invoice.getDueDate() == null) {
            Integer params = parameterRepository.findByName("DEFAULT_CONSIGNMENT_PERIOD").map(Parameters::toInt).orElse(null).orElse(30);
            invoice.updateDueDate(params);
        }
        return consignmentRepository.save(invoice);
    }
}
