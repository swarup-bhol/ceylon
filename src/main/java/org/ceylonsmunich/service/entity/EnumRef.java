package org.ceylonsmunich.service.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.ceylonsmunich.service.serializers.EnumSerializer;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@JsonSerialize(using = EnumSerializer.class)
public class EnumRef {

    @Id
    @Column(name="enum_id")
    private String enumId;

    @OneToMany(mappedBy = "enumRef", fetch = FetchType.EAGER)
    private List<Constant> constants;



}
