package org.ceylonsmunich.service.controllers;

import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.ceylonsmunich.service.entity.*;
import org.ceylonsmunich.service.entity.pdf.invoice.Page;
import org.ceylonsmunich.service.entity.pdf.invoice.Row;
import org.ceylonsmunich.service.entity.repos.ConsignmentRepository;
import org.ceylonsmunich.service.entity.repos.InvoiceRepository;
import org.ceylonsmunich.service.entity.repos.ProductRepository;
import org.ceylonsmunich.service.services.QRGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.time.temporal.ChronoUnit;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PDFController {

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private ConsignmentRepository consignmentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private QRGen qrGen;

    @RequestMapping(path = "/api/pdf/invoice/{id}/download",method = RequestMethod.GET)
    public ResponseEntity<Resource> pdf(@PathVariable("id") Long id) throws IOException, DocumentException, ParseException {

        Optional<Invoice> invoice = repository.findById(id);
        if(invoice.isPresent()){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//new FileOutputStream(outputFolder);

            ITextRenderer renderer = new ITextRenderer();
            String html = processTemplate(invoice.get());
            renderer.setDocumentFromString(html);
            renderer.getFontResolver().addFont("static/fonts/Lato-Regular.ttf", BaseFont.EMBEDDED);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parse = formatter.parse(invoice.get().getDate());
            Calendar c = Calendar.getInstance();
            c.setTime(parse);
            String name = String.format("%4d-%4d_CEYLONS_Rechnung_%s_%2d.%2d.%4d.pdf"
                    ,c.get(Calendar.YEAR),
                    invoice.get().getId(),
                    invoice.get().getCustomer().getBrand(),
                    c.get(Calendar.DATE),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.YEAR)
            );
            renderer.layout();
            renderer.createPDF(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            outputStream.close();
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s",name));
            header.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"*");
            header.add("X-Suggested-Filename",String.format("%s",name));
            header.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,"X-Suggested-Filename");
            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        }

        return ResponseEntity.badRequest().body(null);

    }

    @RequestMapping(path = "/api/pdf/consignment/{id}/download",method = RequestMethod.GET)
    public ResponseEntity<Resource> pdfConsignment(@PathVariable("id") Long id) throws IOException, DocumentException, ParseException {

        Optional<Consignment> consignment = consignmentRepository.findById(id);
        if(consignment.isPresent()){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//new FileOutputStream(outputFolder);

            ITextRenderer renderer = new ITextRenderer();
            String html = processTemplate(consignment.get());
            renderer.setDocumentFromString(html);
            renderer.getFontResolver().addFont("static/fonts/Lato-Regular.ttf", BaseFont.EMBEDDED);

            renderer.layout();
            renderer.createPDF(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            outputStream.close();
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=CON-%6d.pdf",consignment.get().getId()));
            header.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"*");
            header.add("X-Suggested-Filename",String.format("CON-%6d.pdf",consignment.get().getId()));
            header.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,"X-Suggested-Filename");
            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        }

        return ResponseEntity.badRequest().body(null);

    }

    @RequestMapping(path = "/api/pdf/qr/download",method = RequestMethod.GET)
    public ResponseEntity<Resource> pdfQR(@RequestParam("products") String ids) throws IOException, DocumentException {

        List<Long> collect = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<Product> all = (List<Product>) productRepository.findAllById(collect);
        /*Pageable limit = PageRequest.of(0,10);
        org.springframework.data.domain.Page<Product> page = productRepository.findAll(limit);
        List<Product> all = page.get().collect(Collectors.toList());*/
        if(all.size() > 0){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//new FileOutputStream(outputFolder);

            ITextRenderer renderer = new ITextRenderer();
            String html = processTemplate(all);
            renderer.setDocumentFromString(html);

            renderer.layout();
            renderer.createPDF(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            outputStream.close();
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%d qr.pdf",all.size()));
            header.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"*");
            header.add("X-Suggested-Filename",String.format("%d qr.pdf",all.size()));
            header.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,"X-Suggested-Filename");
            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        }

        return ResponseEntity.badRequest().body(null);

    }

    private String processTemplate(Invoice invoice) throws ParseException {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("templates/invoice_templates/");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();

        int totalSize = invoice.getViewItems() + 7;
        List<Page> pages = new ArrayList<>();
        int p_qty = 0;
        double p_cts = 0, p_p = 0,sub_total = 0;
        Page p = new Page();
        for(int i = 0; i < invoice.getViewItems(); i++){
            if(p.getRows().size() == 14){
                p.add(
                        Row.builder().desc("Sum")
                                .cts(String.format("%.2f", p_cts))
                                .price(String.format("%.2f €",p_p))
                                .qty("" + p_qty)
                                .build()
                );
                p_qty = 0;
                p_cts = 0;
                p_p = 0;
                pages.add(p);
                p = new Page();
            }
            InvoicedItem invoicedItem = invoice.getItems().get(i);
            p_qty += invoicedItem.getQty();
            p_cts += invoicedItem.getCts();
            double price = invoicedItem.getPricePerCarat() * invoicedItem.getCts();
            p_p += price;
            sub_total += price;
            p.add(
                    Row.builder().desc(String.format("%s (%s,%s,%s)",invoicedItem.getProduct().getGoods(),invoicedItem.getProduct().getColor(), invoicedItem.getProduct().getShapecut(), invoicedItem.getProduct().getComment()))
                           // .no(invoicedItem.getProduct().getUniqueId())
                            .cts(String.format("%.2f", invoicedItem.getCts()))
                            .ppc(String.format("%.0f €",invoicedItem.getPricePerCarat()))
                            .price(String.format("%.2f €",price))
                            .qty(invoicedItem.getQty().toString())
                            .build()
            );
        }
        if(p.getRows().size() > 8){
            while(p.getRows().size() < 14)
                p.add(Row.builder().build());
            p.add(
                    Row.builder().desc("Sum")
                            .cts(String.format("%.2f", p_cts))
                            .price(String.format("%.2f €",p_p))
                            .qty("" + p_qty)
                            .build()
            );
            pages.add(p);
            p = new Page();
        }
        while(p.getRows().size() < 8)
            p.add(Row.builder().build());

        double total =sub_total + invoice.getAdditionalCharges();
        p.add(Row.builder().desc("Subtotal").price(String.format("%.2f €",invoice.getTotal())).build());
        p.add(Row.builder().desc("Processing and shipping cost").price(String.format("%.2f €",invoice.getAdditionalCharges())).build());
        p.add(Row.builder().desc("Subtotal without VAT").price(String.format("%.2f €",total)).build());
        p.add(Row.builder().desc("Plus VAT").price(String.format("%.2f €",invoice.getVat())).build());
        p.add(Row.builder().desc("Total").style("font-weight: bold;").styleClass("body_bold").price(String.format("%.2f €",total + invoice.getVat())).build());
        p.add(Row.builder().desc("Advance payment").price(String.format("%.2f €",invoice.getAdvancePayment())).build());
        p.add(Row.builder().desc("Remaining balance").price(String.format("%.2f €", sub_total - invoice.getAdvancePayment())).build());

        pages.add(p);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = formatter.parse(invoice.getDate());
        SimpleDateFormat display = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(parse);
        String name = String.format("%4d-%4d_CEYLONS_Rechnung_%s_%2d.%2d.%4d"
                ,c.get(Calendar.YEAR),
                invoice.getId(),
                invoice.getCustomer().getBrand(),
                c.get(Calendar.DATE),
                c.get(Calendar.MONTH),
                c.get(Calendar.YEAR)
        );



        context.setVariable("pages", pages);
        context.setVariable("pageCount", pages.size());
        context.setVariable("customer", invoice.getCustomer());
        context.setVariable("date", display.format(parse));
        context.setVariable("invoice_id",String.format("%4d-%4d",c.get(Calendar.YEAR),invoice.getId()));
        context.setVariable("title", name);
        c.add(Calendar.DATE, 14);
        context.setVariable("due_date",display.format(c.getTime()));
        context.setVariable("taxRegion", invoice.getTaxRegion());
        context.setVariable("totalQty", invoice.getTotalQty());


        return templateEngine.process("template", context);
    }
    private String processTemplate(Consignment consignment) throws ParseException {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("templates/comission_template/");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();

        int totalSize = consignment.getViewItems() + 7;
        List<Page> pages = new ArrayList<>();
        int p_qty = 0;
        double p_cts = 0, p_p = 0,sub_total = 0;
        Page p = new Page();
        for(int i = 0; i < consignment.getViewItems(); i++){
            if(p.getRows().size() == 9){
                p.add(
                        Row.builder().desc("Sum")
                                .cts(String.format("%.2f", p_cts))
                                .price(String.format("%.2f €",p_p))
                                .qty("" + p_qty)
                                .build()
                );
                p_qty = 0;
                p_cts = 0;
                p_p = 0;
                pages.add(p);
                p = new Page();
            }
            InvoicedItem invoicedItem = consignment.getItems().get(i);
            p_qty += invoicedItem.getQty();
            p_cts += invoicedItem.getCts();
            double price = invoicedItem.getPricePerCarat() * invoicedItem.getCts();
            p_p += price;
            sub_total += price;
            p.add(
                    Row.builder().desc(String.format("%s (%s,%s,%s)",invoicedItem.getProduct().getGoods(),invoicedItem.getProduct().getColor(), invoicedItem.getProduct().getShapecut(), invoicedItem.getProduct().getComment()))
                            .no(invoicedItem.getProduct().getUniqueId())
                            .cts(String.format("%.2f", invoicedItem.getCts()))
                            .ppc(String.format("%.0f €",invoicedItem.getPricePerCarat()))
                            .price(String.format("%.2f €",price))
                            .qty(invoicedItem.getQty().toString())
                            .build()
            );
        }

        while(p.getRows().size() < 9)
            p.add(Row.builder().build());

        p.add(Row.builder().desc("Subtotal").style("font-weight: bold;").price(String.format("%.2f €",consignment.getTotal())).build());

        pages.add(p);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = formatter.parse(consignment.getDate());
        Date due = formatter.parse(consignment.getDueDate());
        SimpleDateFormat display = new SimpleDateFormat("dd/MM/yyyy");

        context.setVariable("pages", pages);
        context.setVariable("pageCount", pages.size());
        context.setVariable("customer", consignment.getCustomer());
        context.setVariable("date", display.format(parse));
        context.setVariable("due_date", display.format(parse) + " - " + display.format(due));
        context.setVariable("invoice_id",consignment.getId());

        return templateEngine.process("template", context);
    }


    private String processTemplate(List<Product> products){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("templates/qr/");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        List<Product> collect = products.stream().peek(o -> {
            try {
                o.setQr(qrGen.generate(o.getId() + "/" + o.getUniqueId()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).collect(Collectors.toList());

        Context context = new Context();
        context.setVariable("pages",collect);

        return templateEngine.process("template", context);
    }


}
