package org.ceylonsmunich.service.controllers;

import org.ceylonsmunich.service.entity.Sale;
import org.ceylonsmunich.service.entity.repos.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SaleController {

    @Autowired
    private SaleRepository repository;

    @RequestMapping(value = "/api/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sale> getAll(){ return repository.findAllByOrderByIdAsc();}
}
