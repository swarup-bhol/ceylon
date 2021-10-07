package org.ceylonsmunich.service.entity.pdf.invoice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Row {
    private String desc = "";
    private String no = "";
    private String qty = "";
    private String cts = "";
    private String ppc = "";
    private String price = "";
    private String styleClass= "";
    private String style = "";
}
