package org.ceylonsmunich.service.controllers;

import org.ceylonsmunich.service.config.table.FeTable;
import org.ceylonsmunich.service.config.table.FeTableRepository;
import org.ceylonsmunich.service.entity.ProductOverview;
import org.ceylonsmunich.service.entity.Product;
import org.ceylonsmunich.service.entity.repos.ConfigRepository;
import org.ceylonsmunich.service.entity.repos.ProductOverviewRepository;
import org.ceylonsmunich.service.entity.repos.ProductRepository;
import org.ceylonsmunich.service.parser.ExcelWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    ProductRepository repository;
    @Autowired
    private ProductOverviewRepository overviewRepository;
    @Autowired
    private FeTableRepository configRepository;

    @RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> getAll(){ return repository.findAllByOrderByIdAsc();}

    @RequestMapping(value = "/api/product/save", method = RequestMethod.POST)
    public Product save(@RequestBody Product product){
        Product save =  repository.save(product);
        save.postLoad();
        return save;
    }

    @RequestMapping(value = "/api/products/update", method = RequestMethod.POST)
    public void updateAll(@RequestBody List<Product> products){
        repository.saveAll(products);
    }


    @RequestMapping(value = "/api/products/{id}/overview")
    List<ProductOverview> getOverview(@PathVariable("id") Long id){
        return overviewRepository.findAllByProductId(id);
    }


    @RequestMapping(value = "/api/products/upload", method = RequestMethod.POST)
    public List<Product> upload(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        FeTable feTable = configRepository.findById("product.json").orElse(null);
        if(feTable != null) {
            List<Product> data = ExcelWorkbook.<Product>parse(inputStream, feTable.getColumns(),Product.class);
            inputStream.close();
            return (List<Product>)repository.saveAll(data);
        }
        inputStream.close();
        return null;
    }

}
