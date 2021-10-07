package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ceylonsmunich.service.config.table.DisplayIgnore;
import org.ceylonsmunich.service.serializers.ConstantSerializer;

import javax.persistence.*;

@Data
@Entity
@JsonSerialize(using = ConstantSerializer.class)
public class Constant {

    @Id
    @GeneratedValue
    @DisplayIgnore
    private Long key;

    @Column
    private String value;

    @ManyToOne
    @JoinColumn(name="enum_id", nullable=false)
    private EnumRef enumRef;
}


