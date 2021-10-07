package org.ceylonsmunich.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ceylonsmunich.service.config.table.DisplayAsColumn;
import org.ceylonsmunich.service.config.table.DisplayAsTable;
import org.ceylonsmunich.service.config.table.actions.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DisplayAsTable(value = "currency.json", title = "Currency Table")
@FeAdd
@FeEditable
@FeSearch
@FeFilter
@FeExport
public class Currency {
    @Id
    @DisplayAsColumn(name = "Currency",type = "string",editable = "never")
    private String id;
    @Column
    private String symbol;
    @Column
    private Boolean prefixSymbol = true;
}
