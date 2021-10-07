package org.ceylonsmunich.service.controllers;

import org.ceylonsmunich.service.entity.InvoicedItem;
import org.ceylonsmunich.service.entity.Offer;
import org.ceylonsmunich.service.entity.enums.ItemType;
import org.ceylonsmunich.service.entity.repos.InvoiceItemRepository;
import org.ceylonsmunich.service.entity.repos.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OfferController {

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private OfferRepository offerRepository;

    @RequestMapping(value = "/api/offers", method = RequestMethod.GET)
    public List<Offer> getAll(){
        return offerRepository.findAll();
    }

    @RequestMapping(value = "/api/offer/save", method = RequestMethod.POST)
    public Offer save(@RequestBody Offer offer){
        List<InvoicedItem> collect = offer.getItems().stream().map(o -> {
            o.setType(ItemType.OFFERED);
            return invoiceItemRepository.save(o);
        }).collect(Collectors.toList());
        offer.setItems(collect);
        return offerRepository.save(offer);
    }
}
