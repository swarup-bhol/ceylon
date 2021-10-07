package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.ceylonsmunich.service.config.table.DisplayAsColumn;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.DisplayIgnore;
import org.ceylonsmunich.service.config.table.actions.FeExport;
import org.ceylonsmunich.service.config.table.actions.FeFilter;
import org.ceylonsmunich.service.config.table.actions.FeSearch;
import org.ceylonsmunich.service.entity.enums.ItemType;
import org.ceylonsmunich.service.serializers.InvoiceView;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@Entity
@DisplayAsTable(value = "offer.json", title = "Offers Table")
@FeSearch
@FeFilter
@FeExport
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @DisplayIgnore
    private Customer customer;


    @Transient
    @DisplayAsColumn(name = "Customer", type = "STRING")
    private String customer_summary;

    @Transient
    @DisplayAsColumn(name = "Items", type = "NUMBER")
    private Integer items_count;

    @Column
    @DisplayAsColumn(name="Valid Till", type = "date")
    private String dueDate;

    @Column
    @DisplayAsColumn(name="Issued On", type = "date")
    private String date;

    @OneToMany(fetch = FetchType.EAGER)
    @DisplayIgnore
    private List<InvoicedItem> items;



    public void updateValidity(int days){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            Date d = simpleDateFormat.parse(this.date);
            c.setTime(d);
            c.add(Calendar.DATE, days);
            this.dueDate = simpleDateFormat.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }




    @PrePersist
    private void before(){
        if(this.dueDate == null) {
            this.updateValidity(30);
        }
        for(InvoicedItem item : items){
            item.setType(ItemType.OFFERED);
        }
    }


    @PostLoad
    private void after(){
        this.customer_summary = String.format("%s/%s %s",this.customer.getCompany(),this.customer.getFirstName(), this.customer.getName() );
        this.items_count =  this.items!= null ? this.items.size() : 0;
    }
}
