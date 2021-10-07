package org.ceylonsmunich.service.entity;

import lombok.Data;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.DisplayIgnore;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Immutable
@DisplayAsTable(value = "product_overview.json", title = "Product Overview")
@Table(name="product_overview")
public class ProductOverview {


    @Column(name="description")
    private String description;


    @Column(name="company")
    private String company;


    @Column(name="type")
    private String type;

    @Id
    @DisplayIgnore
    @Column(name = "row_number")
    private Long Id;

    @Column(name="document_id")
    private Long documentId;

    @Column(name="cts")
    private Integer cts;

    @Column(name="price_per_carat")
    private Double pricePerCarat;

    @Column(name="price")
    private Double price;

    @Column(name="qty")
    private Integer qty;

    @Column(name="state")
    private String state;

    @Column(name="product_id")
    private Long productId;


}