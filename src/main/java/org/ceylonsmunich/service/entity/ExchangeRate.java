package org.ceylonsmunich.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ceylonsmunich.service.config.table.DisplayAsColumn;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.DisplayNumberFormat;
import org.ceylonsmunich.service.config.table.actions.*;
import org.ceylonsmunich.service.entity.id.ExchangeRateId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(ExchangeRateId.class)
@DisplayAsTable(value = "exchange.json", title = "Exchange Rate Table")
@FeAdd
@FeEditable
@FeSearch
@FeFilter
@FeExport
public class ExchangeRate {
    @Id
    @DisplayAsColumn(name = "From",type = "string",enumName = "currency",editable = "never")
    private String fromCurrency;
    @Id
    @DisplayAsColumn(name = "To (Unit)",type = "string",enumName = "currency",editable = "never")
    private String toCurrency;
    @Column
    @DisplayNumberFormat(precision = 2)
    private Double rate;

}
