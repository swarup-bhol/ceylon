package org.ceylonsmunich.service.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.ceylonsmunich.service.entity.Invoice;
import org.ceylonsmunich.service.entity.InvoicedItem;
import org.ceylonsmunich.service.entity.enums.InvoiceState;
import org.ceylonsmunich.service.entity.repos.CustomerRepository;
import org.ceylonsmunich.service.entity.repos.InvoiceItemRepository;
import org.ceylonsmunich.service.entity.repos.InvoiceRepository;
import org.ceylonsmunich.service.entity.repos.ProductRepository;
import org.ceylonsmunich.service.serializers.InvoiceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class InvoiceController {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @JsonView(InvoiceView.DefaultOnly.class)
    @RequestMapping(value = "/api/invoices", method = RequestMethod.GET)
    public List<Invoice> getAll(){
        return invoiceRepository.findByOrderById();
    }

    @JsonView(InvoiceView.Details.class)
    @RequestMapping(value = "/api/invoices/{id}")
    public Invoice find(@PathVariable Long id){
        return invoiceRepository.findById(id).orElse(null);
    }

    @JsonView(InvoiceView.Details.class)
    @RequestMapping(value = "/api/invoices/update")
    public Invoice save(@RequestBody Invoice invoice){
        List<InvoicedItem> items = new ArrayList<>();
        for(InvoicedItem item: invoice.getItems()){
            items.add(invoiceItemRepository.save(item));
        }
        invoice.setItems(items);
        return invoiceRepository.save(invoice);
    }

    @JsonView(InvoiceView.DefaultOnly.class)
    @RequestMapping(value = "/api/invoices/complete")
    public Invoice complete(@RequestBody Invoice id){
        Optional<Invoice> invoice = invoiceRepository.findById(id.getId());
        if(invoice.isPresent()){
            Invoice i = invoice.get();
            i.setState(InvoiceState.COMPLETE);
            if(id.getCompletedOn() != null){
                i.setCompletedOn(id.getCompletedOn());
            }else{
                i.setCompletedOn(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }
            invoiceRepository.save(i);
            return i;
        }
        return null;
    }


    @JsonView(InvoiceView.DefaultOnly.class)
    @RequestMapping(value = "/api/invoices/refund")
    public Invoice refund(@RequestBody Invoice id){
        Optional<Invoice> invoice = invoiceRepository.findById(id.getId());
        if(invoice.isPresent()){
            Invoice i = invoice.get();
            i.setState(InvoiceState.REFUNDED);
            invoiceRepository.save(i);
            return i;
        }
        return null;
    }
}
