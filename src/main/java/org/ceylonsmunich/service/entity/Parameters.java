package org.ceylonsmunich.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ceylonsmunich.service.config.table.DisplayAsTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "system_parameters")
@DisplayAsTable(value = "parameters.json", title = "System Parameters")
public class Parameters {

    @Id
    @Column
    private String name;

    @Column
    private String value;

    @Column
    private String description;

    public Optional<Integer> toInt(){
        return Optional.ofNullable(value).filter(p->p.matches("-?\\d+(\\.\\d+)?")).map(Integer::parseInt);
    }

    public Optional<Double> toDouble(){
        return Optional.ofNullable(value).filter(p->p.matches("-?\\d+(\\.\\d+)?")).map(Double::parseDouble);
    }
}
