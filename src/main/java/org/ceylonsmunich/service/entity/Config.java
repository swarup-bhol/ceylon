package org.ceylonsmunich.service.entity;

import lombok.Data;
import org.ceylonsmunich.service.config.table.DisplayIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Config implements Serializable {

    @Id
    @DisplayIgnore
    private String id;

    @Column
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String config;
}
