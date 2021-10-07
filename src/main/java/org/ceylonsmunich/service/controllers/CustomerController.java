package org.ceylonsmunich.service.controllers;

import org.ceylonsmunich.service.config.table.FeTable;
import org.ceylonsmunich.service.config.table.FeTableRepository;
import org.ceylonsmunich.service.entity.*;
import org.ceylonsmunich.service.entity.repos.CustomerRepository;
import org.ceylonsmunich.service.parser.ExcelWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    CustomerRepository repository;

    @Autowired
    FeTableRepository configRepository;

    @RequestMapping(value = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Customer> getAll(){ return repository.findAllByOrderByIdAsc();}


    @RequestMapping(value = "/api/customers/{company}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Customer> getAllByCompany(@PathVariable("company") String company){ return repository.findAllByCompany(company);}


    @RequestMapping(value = "/api/customers/update", method = RequestMethod.POST)
    public void updateAll(@RequestBody List<Customer> sales){

        repository.saveAll(sales);
    }


    @RequestMapping(value = "/api/customer/save", method = RequestMethod.POST)
    public Customer update(@RequestBody Customer sales){

        return repository.save(sales);
    }


    @RequestMapping(value = "/api/customer/upload", method = RequestMethod.POST)
    public List<Customer> upload(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        FeTable feTable = configRepository.findById("customer.json").orElse(null);
        if(feTable != null) {
            List<Customer> data = ExcelWorkbook.<Customer>parse(inputStream, feTable.getColumns(),Customer.class);
            inputStream.close();
            return (List<Customer>)repository.saveAll(data);
        }
        inputStream.close();
        return null;
    }
}
