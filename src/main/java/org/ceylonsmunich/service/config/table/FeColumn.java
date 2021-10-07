package org.ceylonsmunich.service.config.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class FeColumn {
    @Id
    private Long id;

    @Column
    @JsonProperty("dataIndex")
    private String name;
    @Column
    @JsonProperty("title")
    private String displayName;
    @Column
    @JsonProperty("type")
    private String type;
    @Column
    private String enumName;
    @Column
    private String align="center";

    @Column
    @JsonProperty("sorter")
    private Boolean sortable = true;
    @Column
    private Boolean filter = true;

    @Column
    private Boolean visible;

    @Column
    private Boolean editable;

    @Column(name="col_order")
    private Integer order;

    @Column
    private Boolean thousandSeparator = false;

    @Column
    @JsonProperty("decimal")
    private Integer precision;

    @Column
    private String currencyId;

    @Column
    private Boolean derivedCurrency;

    @Column
    private String prefix;

    @Column
    private String suffix;

    @Column
    private Integer schemaIndex;
    @Column
    private Integer width = 200;

}
