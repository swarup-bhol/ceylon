package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.actions.FeExport;
import org.ceylonsmunich.service.config.table.actions.FeFilter;
import org.ceylonsmunich.service.config.table.actions.FeSearch;
import org.ceylonsmunich.service.entity.enums.ConsignmentState;
import org.ceylonsmunich.service.entity.enums.ItemType;
import org.ceylonsmunich.service.serializers.ConsignmentView;
import org.ceylonsmunich.service.serializers.InvoiceView;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Data
@DisplayAsTable(value = "consignment.json", title = "Consignments")
@FeSearch
@FeFilter
@FeExport
public class Consignment {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(targetEntity = InvoicedItem.class, fetch = FetchType.EAGER)
    private List<InvoicedItem> items;

    @OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
    private Customer customer;

    @JsonView(ConsignmentView.DefaultOnly.class)
    @JsonProperty(value = "customer_summary")
    public String getCustomerForView(){
        return String.format("%s",this.customer.getCompany());
    }

    @JsonView(ConsignmentView.DefaultOnly.class)
    @JsonProperty(value = "items_count")
    public int getViewItems(){
        return this.items!= null ? this.items.size() : 0;
    }


    @Column
    private String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

    @Column
    private String dueDate;

    @Column
    private Double total;

    @Enumerated(EnumType.STRING)
    private ConsignmentState state = ConsignmentState.AWAY;


    public void updateDueDate(int days){
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
            this.updateDueDate(30);
        }
        total = 0.0;
        for(InvoicedItem item : items){
            total += (item.getPricePerCarat() * item.getCts());
            item.setType(ItemType.CONSIGNED);
        }
    }

    @PostLoad
    private void after(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(date != null && dueDate != null && state == ConsignmentState.AWAY){
            try {
                Date d1  = simpleDateFormat.parse(date);
                Date d2 = simpleDateFormat.parse(dueDate);
                if(d2.compareTo(d1) <0)
                    this.state = ConsignmentState.OVERDUE;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
