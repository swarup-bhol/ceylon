package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.ceylonsmunich.service.config.table.*;
import org.ceylonsmunich.service.config.table.actions.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@DisplayAsTable(value = "product.json", title = "Products")
@FeColumnConfigure
@FeAdd
@FeEditable
@FeSearch
@FeFilter
@FeExport
@GenerateSchema(value = "product_schema.json", name = "Product Schema")
public class Product implements Serializable {
    @Id
    @GeneratedValue
    @DisplayIgnore
    private Long id;

    @Column
    private String owner;

    @Column
    private String uniqueId;
    @Column
    private String goods;
    @Column
    private String type;
    @Column
    private String comment;
    @Column
    private Double cts = 2.4;
    @Column
    private Double qty;
    @Column
    private String color;
    @Column
    private String colorDetail;
    @Column
    private String category;
    @Column
    private String shapecut;
    @Column
    private String dimentions;
    @Column
    private String origin;
    @Column
    private String report;
    @Column
    private Long reportNo;
    @Column
    @DisplayAsColumn(name="Report Date", type = "date")
    private String reportDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    @Column
    private Boolean fair;
    @Column
    private String lotBatchSetPair;

    @Column(name="import")
    private String import_id;
    @Column
    private String request;
    @Column
    private String note;

    @Column
    @DisplayAsColumn(name="Last Changed", type = "date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastChange = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
/*
    @OneToOne(fetch = FetchType.EAGER)
    @JsonUnwrapped(prefix = "saleInformation_")
    private SaleInformation saleInformation;
*/

    @Column
    @DisplayAsColumn(name = "originCurrency", type = "string",enumName = "currency")
    private String originCurrency="LKR";

    @DisplayIgnore
    @Formula("(select c.value from system_parameters c where c.name = 'BASE_CURRENCY' limit 1)")
    private String baseCurrency;

    @DisplayIgnore
    @Formula("(select c.symbol from currency c where c.id = origin_currency)")
    private String originSymbol;
    @DisplayIgnore
    @Formula("(select case when c.prefix_symbol = true then true else false end from currency c where c.id = origin_currency)")
    private Boolean originPrefix;
    @DisplayIgnore
    @Formula("(select c.symbol from currency c where c.id = (select s.value from system_parameters s where s.name = 'BASE_CURRENCY' limit 1) limit 1)")
    private String baseSymbol;
    @DisplayIgnore
    @Transient
    @Formula("(select case when c.prefix_symbol = true then true else false end from currency c where c.id = (select c.value from system_parameters c where c.name = 'BASE_CURRENCY' limit 1) limit 1)")
    private Boolean basePrefix;


    @Formula("(select s.due_dates from product_summary s where s.product_id = id and  s.item_type = 'CONSIGNED' limit 1)")
    private String awayTill;

    @Formula("(select s.qty from product_summary s where s.product_id = id and s.item_type = 'CONSIGNED' limit 1)")
    private Integer consigned;

    @Formula("(select s.qty from product_summary s where s.product_id = id and s.item_type = 'INVOICED' limit 1)")
    private Integer invoiced;


    @Formula("(select s.cts from product_summary s where s.product_id = id and s.item_type = 'CONSIGNED' limit 1)")
    private Double consignedCts;

    @Formula("(select s.cts from product_summary s where s.product_id = id and s.item_type = 'INVOICED' limit 1)")
    private Double invoicedCts;
/*

    @ManyToOne
    @JoinColumnOrFormula(formula = @JoinFormula(value = "(select * from exchange_rate e where e.from_currency = originCurrency and e.to_currency = (select c.value from system_parameters c where c.name = 'BASE_CURRENCY'))", referencedColumnName = "originCurrency"))
    private ExchangeRate rate;
*/
    @DisplayIgnore
    @Formula("(select e.rate from exchange_rate e where e.from_currency = origin_currency and e.to_currency = 'EUR')")
    private Double rate ;

    @Column
    @DisplayCurrency(id="originCurrency", derived = true)
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    private Double buyPrice;

    @Transient
    @DisplayCurrency(id="originCurrency", derived = true)
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Buy Price/ct",editable = "never",type = "float")
    private Double buyPricePerCt;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Buy Price €",editable = "never",type = "float")
    private Double buyPriceInBaseCurrency;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Buy Price €/ct",editable = "never",type = "float")
    private Double buyingPriceBasePerCarat;


    @Column
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    private Double privatePrice;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Private Price/ct",editable = "never",type = "float")
    private Double privatePricePerCarat;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Private Profit",editable = "never",type = "float")
    private Double privateProfit;
    @Transient
    @DisplaySuffix("%")
    @DisplayNumberFormat(precision = 2)
    @DisplayAsColumn(name = "Private Profit %",editable = "never",type = "float")
    private Double privateProfitPercentage;

    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @Formula("(select s.price from product_summary s where s.product_id = id and s.item_type = 'INVOICED' limit 1)")
    private Double sellPrice;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Sale Price/ct",editable = "never",type = "float")
    private Double sellPricePerCarat;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Sale Profit",editable = "never",type = "float")
    private Double sellProfit;
    @Transient
    @DisplaySuffix("%")
    @DisplayNumberFormat(precision = 2)
    @DisplayAsColumn(name = "Sale Profit %",editable = "never",type = "float")
    private Double saleProfitPercentage;

    @Column
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    private Double minPrice;

    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Min Price/ct",editable = "never",type = "float")
    private Double minPricePerCarat;
    @Transient
    @DisplayCurrency
    @DisplayNumberFormat(thousandSeparators = true,precision = 2)
    @DisplayAsColumn(name = "Min Profit",editable = "never",type = "float")
    private Double minProfit;
    @Transient
    @DisplaySuffix("%")
    @DisplayNumberFormat(precision = 2)
    @DisplayAsColumn(name = "Min Profit %",editable = "never",type = "float")
    private Double minProfitPercentage;

    @Transient
    @DisplayIgnore
    private String qr;

    @PostLoad
    public void postLoad(){
        if(originCurrency != null && originCurrency.equals(baseCurrency)){
            this.rate = 1.0;
        }

        this.buyPricePerCt = this.buyPrice != null && this.cts != null && this.cts != 0 ? this.buyPrice/this.cts : null;
        this.buyPriceInBaseCurrency = this.buyPrice!= null && this.rate!= null ? this.buyPrice * this.rate: null;
        this.buyingPriceBasePerCarat = this.buyPriceInBaseCurrency== null || this.cts == null ? null:this.buyPriceInBaseCurrency/this.cts;

        this.privatePricePerCarat = this.privatePrice==null||this.cts==null?null:this.privatePrice/this.cts;
        this.privateProfit = this.buyPriceInBaseCurrency== null || this.privatePrice == null ? null: this.privatePrice - this.buyPriceInBaseCurrency;
        this.privateProfitPercentage = this.privateProfit == null?null:this.privateProfit * 100/this.privatePrice;

        this.sellPricePerCarat = this.sellPrice==null||this.invoicedCts==null?null:this.sellPrice/(this.invoicedCts);
        this.sellProfit = this.buyPriceInBaseCurrency== null || this.sellPrice == null ? null: this.sellPrice - this.buyPriceInBaseCurrency;
        this.saleProfitPercentage = this.sellProfit== null ? null : this.sellProfit * 100/ this.sellPrice;

        this.minPricePerCarat = this.minPrice==null|| this.cts==null?null:this.minPrice/this.cts;
        this.minProfit = this.buyPriceInBaseCurrency== null ||this.minPrice==null?null:this.minPrice - this.buyPriceInBaseCurrency;
        this.minProfitPercentage = this.minProfit==null?null:this.minProfit * 100/this.minPrice;

    }

}
