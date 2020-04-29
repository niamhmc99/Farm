
package com.example.farm.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmissionData {

    @SerializedName("detail")
    @Expose
    private List<String> detail = null;
    @SerializedName("pdf_url")
    @Expose
    private String pdfUrl;
    @SerializedName("title")
    @Expose
    private String title;

    public List<String> getDetail() {
        return detail;
    }

    public void setDetail(List<String> detail) {
        this.detail = detail;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
