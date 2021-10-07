package org.ceylonsmunich.service.entity.pdf.invoice;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Page {

    private List<Row> rows = new ArrayList<>();

    public void add(Row r){ rows.add(r);}
}
