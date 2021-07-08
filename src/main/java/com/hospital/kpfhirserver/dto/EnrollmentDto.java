package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EnrollmentDto {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("connectionStatus")
    @Expose
    private String connectionStatus;
    @SerializedName("connectionTemplateID")
    @Expose()
    private String connectionTemplateID;
    @SerializedName("connectionTemplateName")
    @Expose
    private String connectionTemplateName;

    @SerializedName("icd10Codes")
    @Expose
    private List<IcdCodesDto> icdCodesDto;

    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("orderNumber")
    @Expose
    private String orderNumber;
    public EnrollmentDto() {
    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public EnrollmentDto(String code, String startDate, String endDate, String connectionStatus, String connectionTemplateID, String connectionTemplateName,
                         List<IcdCodesDto> icdCodesDto, String duration, String orderNumber) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.connectionStatus = connectionStatus;
        this.connectionTemplateID = connectionTemplateID;
        this.connectionTemplateName = connectionTemplateName;
        this.icdCodesDto = icdCodesDto;
        this.duration = duration;
        this.orderNumber = orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getConnectionTemplateID() {
        return connectionTemplateID;
    }

    public void setConnectionTemplateID(String connectionTemplateID) {
        this.connectionTemplateID = connectionTemplateID;
    }

    public String getConnectionTemplateName() {
        return connectionTemplateName;
    }

    public void setConnectionTemplateName(String connectionTemplateName) {
        this.connectionTemplateName = connectionTemplateName;
    }

    public List<IcdCodesDto> getIcdCodesDto() {
        return icdCodesDto;
    }

    public void setIcdCodesDto(List<IcdCodesDto> icdCodesDto) {
        this.icdCodesDto = icdCodesDto;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "EnrollmentDto{" +
                "code='" + code + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", connectionStatus='" + connectionStatus + '\'' +
                ", connectionTemplateID='" + connectionTemplateID + '\'' +
                ", connectionTemplateName='" + connectionTemplateName + '\'' +
                ", icdCodesDto=" + icdCodesDto +
                ", duration='" + duration + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                '}';
    }
}
