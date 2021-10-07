package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.ceylonsmunich.service.config.table.DisplayAsColumn;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.DisplayIgnore;
import org.ceylonsmunich.service.config.table.GenerateSchema;
import org.ceylonsmunich.service.config.table.actions.*;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@Entity
@DisplayAsTable(value = "customer.json", title = "Customers")
@FeColumnConfigure
@FeAdd
@FeEditable
@FeSearch
@FeFilter
@FeExport
@GenerateSchema(value = "customer_schema.json", name = "Customer Schema")
public class Customer implements Serializable {
    @Id
    @DisplayIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private String brand;
    @Column private String company;
    @Column private String firstName;
    @Column private String name;
    @Column private String mobile;
    @Column private String companyTelephone;
    @Column private String companyMobile;
    @Column private String personalMail;
    @Column private String companyMail;
    @Column private String url;
    @Column private String street;
    @Column private String zip;
    @Column private String residence;
    @Column private String country;
    @Column private Boolean deliverToInvoiceAddress = true;
    @Column private String deliveryStreet;
    @Column private String deliveryZip;
    @Column private String deliveryResidence;
    @Column private String deliveryCountry;
    @Column private String vatId;
    @Column private String taxRegion;
    @Column private String type;
    @Column private String category;
    @Column private String function;
    @Column private String gender;
    @Column private String title;
    @Column private String contact;
    @Column private String pocLv1;
    @Column private String pocLv2;
    @Column private String focus;
    @Column private String relevance;
    @Column private String language;
    @Column private String newsletter;
    @Column private String note;

    @Column
    @DisplayAsColumn(name="Last Changed", type = "date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastChanged = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @Column
    @DisplayAsColumn(name="Date of Listing", type = "date")
    private String dateOfListing;


    @Column private String companyId;


    @PrePersist
    public void prePersist(){
        this.lastChanged = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @PostLoad
    public void postLoad(){
        if(this.deliverToInvoiceAddress == null || this.deliverToInvoiceAddress){
            this.deliveryZip = this.zip;
            this.deliveryCountry = this.country;
            this.deliveryResidence = residence;
            this.deliveryStreet = street;
        }
    }
}
