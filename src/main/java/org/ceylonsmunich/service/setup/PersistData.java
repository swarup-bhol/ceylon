package org.ceylonsmunich.service.setup;

import org.ceylonsmunich.service.entity.*;
import org.ceylonsmunich.service.entity.enums.InvoiceState;
import org.ceylonsmunich.service.entity.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(4)
public class PersistData implements CommandLineRunner {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    SaleRepository saleRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    InvoiceItemRepository invoiceItemRepository;

    @Autowired
    ConsignmentRepository consignmentRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Value("${application.database.dummy.data:#{false}}")
    private boolean restore;


    @Value("${application.database.dummy.data.count:#{0}}")
    private int rowCount;

    @Override
    public void run(String... args) throws Exception {

        if(restore){
            invoiceRepository.deleteAll();
            consignmentRepository.deleteAll();
            invoiceItemRepository.deleteAll();
            saleRepository.deleteAll();
            productRepository.deleteAll();
            customerRepository.deleteAll();

            List<Product> productList = new ArrayList<>();
            List<Customer> customers = new ArrayList<>();

            for(int i = 0; i< rowCount; ++i){
                Product product = new Product();
                product.setOrigin("Ceylon");
                product.setQty(14.0);
                product.setBuyPrice(213.44);
                product.setPrivatePrice(1234.4);
                product.setMinPrice(245.33);
                product.setSellPrice(3244.33);
                product.setGoods("Saphaire");
                product.setColor("Blue");
                product.setShapecut("Oval");
                product.setDimentions("22.20 x 22.45 x 88.33");
                product.setCts(22.56);
                product.setReport("OOSFDJ");
                product.setType("ESub");
                product.setComment("NH");
                product.setLotBatchSetPair(i%2 == 0 ? "Lot" : "Batch");
                product.setUniqueId("ASE-JNJ");


                Customer customer = new Customer();
                customer.setCompanyId("C_" + i);

                customer.setCompany("MIB");


                customer.setGender("F");
                customer.setFirstName("Pamela");
                customer.setMobile("+9411214321832");
                customer.setName("Anderson");
                customer.setPersonalMail("pamela.anderson@gmail.com");

                customers.add(customerRepository.save(customer));

                productList.add(productRepository.save(product));
/*
                Invoice invoice = new Invoice();
                invoice.setAdditionalCharges(100.0);
                List<InvoicedItem> collect = productList.stream().map(o -> {
                    InvoicedItem item = new InvoicedItem();
                    item.setProduct(o);
                    item.setPrice(5999.0);
                    item.setQty(1);
                    item.setCts(1.0);
                    return invoiceItemRepository.save(item);
                }).collect(Collectors.toList());
                invoice.setItems(collect);
                invoice.setAdvancePayment(200.0);
                invoice.setVat(176.0);
                invoice.setState(InvoiceState.PENDING);
                invoice.setCustomer(customer);
                invoiceRepository.save(invoice);*/
            }
        }
    }

}
