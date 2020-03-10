package com.example.farm.models;

public class InvoiceExpense {
    private String id;
    private String invoiceType;
    private String category;
    private String image;
    private String userId;

    public InvoiceExpense() { //empty constructor

    }

    //this constructor includes animal profile pic and userID for RecycleView
    public InvoiceExpense(String id, String invoiceType, String category, String image, String userId) {
        this.id = id;
        this.invoiceType = invoiceType;
        this.category = category;
        this.image = image;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId =userId;
    }
}
