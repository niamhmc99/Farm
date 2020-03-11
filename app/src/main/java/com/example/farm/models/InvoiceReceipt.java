package com.example.farm.models;

public class InvoiceReceipt {
    private String id;
    private String type;
    private String category;
    private String image;
    private String userId;

    public InvoiceReceipt() {

    }

    public InvoiceReceipt(String id, String type, String category, String image, String userId) {
        this.id = id;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
