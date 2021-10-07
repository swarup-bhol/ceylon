package org.ceylonsmunich.service.entity;

import lombok.Data;
import org.ceylonsmunich.service.config.table.DisplayCurrency;
import org.ceylonsmunich.service.config.table.DisplayIgnore;
import org.ceylonsmunich.service.config.table.DisplayNumberFormat;
import org.ceylonsmunich.service.entity.enums.ItemType;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class InvoicedItem implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(targetEntity = Product.class)
    private Product product;

    @Column
    private Integer qty = 0;

    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double pricePerCarat = 0.0;


    @Transient
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    @Formula("cts:numeric * pricePerCarat:numeric")
    private Double price = 0.0;

    @Column
    @DisplayNumberFormat(precision = 2)
    private Double cts = 0.0;

    @Column
    @Enumerated(EnumType.STRING)
    @DisplayIgnore
    private ItemType type = ItemType.INVOICED;


}
