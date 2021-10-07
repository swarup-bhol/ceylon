package org.ceylonsmunich.service.serializers;

public class ConsignmentView {
    public static class Default{}
    public static class DefaultOnly extends InvoiceView.Default {}
    public static class Details extends InvoiceView.Default {}
}
