package com.example.farm.models;

public class InvoiceReceipt {
    private String id;
    private String invoiceReceiptType;
    private String category;
    private String image;
    private String user_id;

    public InvoiceReceipt() {

    }

    public InvoiceReceipt(String id, String invoiceReceiptType, String category, String image, String user_id) {
        this.id = id;
        this.invoiceReceiptType = invoiceReceiptType;
        this.category = category;
        this.image = image;
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceReceiptType() {
        return invoiceReceiptType;
    }

    public void setInvoiceReceiptType(String type) {
        this.invoiceReceiptType = invoiceReceiptType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
