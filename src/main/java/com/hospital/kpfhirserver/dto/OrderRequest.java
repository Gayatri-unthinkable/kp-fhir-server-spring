package com.hospital.kpfhirserver.dto;

import java.util.List;

public class OrderRequest {

    private String assignedMemberID;
    private List<BillingCodesDto> billingCodes;
    private String connectionTemplateID;
    private String customParticipantID;
    private String dob;
    private String email;
    private String firstName;
    private List<IcdCodeRequest> icd10Codes;
    private String implantedDevice;
    private String lastName;
    private String notes;
    private String orderNumber;
    private String phone;
    private Boolean prescribed;
    private String sex;

    public OrderRequest() {
    }

    public OrderRequest(String assignedMemberID, List<BillingCodesDto> billingCodes, String connectionTemplateID,
                        String customParticipantID, String dob, String email,
                        String firstName, List<IcdCodeRequest> icd10Codes,
                        String implantedDevice, String lastName, String notes,
                        String orderNumber, String phone, Boolean prescribed, String sex) {
        this.assignedMemberID = assignedMemberID;
        this.billingCodes = billingCodes;
        this.connectionTemplateID = connectionTemplateID;
        this.customParticipantID = customParticipantID;
        this.dob = dob;
        this.email = email;
        this.firstName = firstName;
        this.icd10Codes = icd10Codes;
        this.implantedDevice = implantedDevice;
        this.lastName = lastName;
        this.notes = notes;
        this.orderNumber = orderNumber;
        this.phone = phone;
        this.prescribed = prescribed;
        this.sex = sex;
    }

      public String getAssignedMemberID() {
        return assignedMemberID;
    }

    public void setAssignedMemberID(String assignedMemberID) {
        this.assignedMemberID = assignedMemberID;
    }

    public List<BillingCodesDto> getBillingCodes() {
        return billingCodes;
    }

    public void setBillingCodes(List<BillingCodesDto> billingCodes) {
        this.billingCodes = billingCodes;
    }

    public String getConnectionTemplateID() {
        return connectionTemplateID;
    }

    public void setConnectionTemplateID(String connectionTemplateID) {
        this.connectionTemplateID = connectionTemplateID;
    }

    public String getCustomParticipantID() {
        return customParticipantID;
    }

    public void setCustomParticipantID(String customParticipantID) {
        this.customParticipantID = customParticipantID;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<IcdCodeRequest> getIcd10Codes() {
        return icd10Codes;
    }

    public void setIcd10Codes(List<IcdCodeRequest> icd10Codes) {
        this.icd10Codes = icd10Codes;
    }

    public String getImplantedDevice() {
        return implantedDevice;
    }

    public void setImplantedDevice(String implantedDevice) {
        this.implantedDevice = implantedDevice;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPrescribed() {
        return prescribed;
    }

    public void setPrescribed(Boolean prescribed) {
        this.prescribed = prescribed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "assignedMemberID='" + assignedMemberID + '\'' +
                ", billingCodes=" + billingCodes +
                ", connectionTemplateID='" + connectionTemplateID + '\'' +
                ", customParticipantID='" + customParticipantID + '\'' +
                ", dob='" + dob + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", icd10Codes=" + icd10Codes +
                ", implantedDevice='" + implantedDevice + '\'' +
                ", lastName='" + lastName + '\'' +
                ", notes='" + notes + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", phone='" + phone + '\'' +
                ", prescribed=" + prescribed +
                ", sex='" + sex + '\'' +
                '}';
    }
}
