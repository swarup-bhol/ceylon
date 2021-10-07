package org.ceylonsmunich.service.entity;

import lombok.Data;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.DisplayCurrency;
import org.ceylonsmunich.service.config.table.DisplayNumberFormat;
import org.ceylonsmunich.service.config.table.DisplaySuffix;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@DisplayAsTable(value = "sales_view.json", title = "Sales Overview")
@Table(name="sales")
public class Sale {
    @Column private Integer year;
    @Column private String status;
    @Column private String invoiceDate;
    @Column private String paymentTarget;
    @Column private String transferDate;
    @Column private Integer monthTransfer;
    @Column private Integer quarterTransfer;
    @Column private String contact;
    @Column private Long invoiceNo;
    @Column private String company;
    @Column private String name;
    @Column private String country;
    @Column private String type;
    @Column private String pocLv1;
    @Column private String pocLv2;
    @Column private String posLv1;
    @Column private String posLv2;
    @Column private String rtb;
    @Column private String fairTrade;
    @Column private String goods;
    @Id private Long id;
    @Column private Integer qty;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    private Double cts;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    private Double vatPercentage;
    @Column private String taxRegion;
    @Column private String vatId;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double net;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double vat;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double gross;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double advancedPayment;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double remainingBalance;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double buy;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double sale;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplayCurrency
    private Double profit;
    @Column
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @DisplaySuffix("%")
    private Double marginPercentage;
}
