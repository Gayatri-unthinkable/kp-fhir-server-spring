package com.hospital.kpfhirserver.dto;

public class BillingCodesDto {

    private Boolean billable;
    private String description;
    private String id;
    private String name;

    public BillingCodesDto() {
    }

    public BillingCodesDto(Boolean billable, String description, String id, String name) {
        this.billable = billable;
        this.description = description;
        this.id = id;
        this.name = name;
    }

    public Boolean getBillable() {
        return billable;
    }

    public void setBillable(Boolean billable) {
        this.billable = billable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BillingCodesDto{" +
                "billable=" + billable +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
