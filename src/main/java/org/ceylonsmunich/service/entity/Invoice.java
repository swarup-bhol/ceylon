package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.ceylonsmunich.service.config.table.*;
import org.ceylonsmunich.service.config.table.actions.FeExport;
import org.ceylonsmunich.service.config.table.actions.FeFilter;
import org.ceylonsmunich.service.config.table.actions.FeSearch;
import org.ceylonsmunich.service.entity.enums.InvoiceState;
import org.ceylonsmunich.service.entity.enums.InvoiceType;
import org.ceylonsmunich.service.entity.enums.ItemType;
import org.ceylonsmunich.service.serializers.InvoiceView;
import org.hibernate.annotations.Formula;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Data
@Entity
@DisplayAsTable(value = "invoice.json", title = "Invoice Table")
@FeSearch
@FeFilter
@FeExport
public class Invoice {
    @Id
    @GeneratedValue
    @JsonView(InvoiceView.Default.class)
    private Long id;

    @OneToOne(targetEntity = Customer.class,fetch = FetchType.EAGER)
    @JsonView(InvoiceView.Details.class)
    @JsonProperty("customer")
    @DisplayIgnore
    private Customer customer;

    @JsonView(InvoiceView.DefaultOnly.class)
    @JsonProperty(value = "customer_summary")
    public String getCustomerForView(){
        return String.format("%s",this.customer.getCompany());
    }

    @OneToMany(targetEntity = InvoicedItem.class,fetch = FetchType.EAGER)
    @JsonView(InvoiceView.Details.class)
    @JsonProperty("items")
    @DisplayIgnore
    private List<InvoicedItem> items;

    @JsonView(InvoiceView.DefaultOnly.class)
    @JsonProperty(value = "items_count")
    public int getViewItems(){
        return this.items!= null ? this.items.size() : 0;
    }

    @JsonView(InvoiceView.DefaultOnly.class)
    @JsonProperty(value = "totalQty")
    public int getTotalQty(){
        return this.items!= null ? this.items.stream().map(InvoicedItem::getQty).reduce(0, Integer::sum) : 0;
    }

    @Column
    @DisplayCurrency
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @JsonView(InvoiceView.Default.class)
    private Double additionalCharges = 0.0;

    @Column
    private String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    @Column
    @DisplayCurrency
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @JsonView(InvoiceView.Default.class)
    private Double vat = 0.0;

    @Column
    @DisplayCurrency
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @JsonView(InvoiceView.Default.class)
    private Double vatPercentage = 0.0;

    @Column
    @DisplayCurrency
    @DisplayNumberFormat(precision = 2,thousandSeparators = true)
    @JsonView(InvoiceView.Default.class)
    private Double advancePayment;

    @Transient
    @Formula("total:numeric - advance_payment:numeric")
    @JsonView(InvoiceView.Default.class)
    private Double remaining;

    @Column
    @DisplayNumberFormat(precision = 2, thousandSeparators = true)
    @JsonView(InvoiceView.Default.class)
    private Double total=0.0;

    @Enumerated(EnumType.STRING)
    @JsonView(InvoiceView.Default.class)
    private InvoiceState state = InvoiceState.PENDING;

    @Column
    private String taxRegion;

    @Column
    private Long consignmentId;

    @Column
    @DisplayAsColumn(name="Completed On", type = "date")
    private String completedOn;


    @Column
    private String posLv1;
    @Column
    private String posLv2;
    @Column
    private String rtb;
    @Column
    private Boolean fair;

    @Enumerated(EnumType.STRING)
    private InvoiceType type = InvoiceType.B2C;

    @Column
    private String dueDate;

    @PrePersist
    private void beforePersist(){
        total = 0.0;
        for(InvoicedItem item : items){
            total += (item.getPricePerCarat() * item.getCts());
            item.setType(ItemType.INVOICED);
        }
        total += (vat + additionalCharges);
        if(total.equals(advancePayment)) {
            this.state = InvoiceState.COMPLETE;
            if(this.completedOn == null)
                this.completedOn = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        if(this.date == null){
            this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }
/*
    @OneToOne
    @JsonView(InvoiceView.Details.class)
    @DisplayIgnore
    private Address address;*/
}
