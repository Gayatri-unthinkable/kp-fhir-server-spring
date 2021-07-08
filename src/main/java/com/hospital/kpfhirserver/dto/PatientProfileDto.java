package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientProfileDto {

    @SerializedName("id")
    @Expose
    private String patientId;

    @SerializedName("customParticipantID")
    @Expose
    private String mrn;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("phone")
    @Expose
    private String phone;

    public PatientProfileDto() {
    }

    public PatientProfileDto(String patientId, String mrn, String dob, String email, String firstName,
                             String lastName, String sex, String phone) {
        this.patientId = patientId;
        this.mrn = mrn;
        this.dob = dob;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.phone = phone;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "PatientDto{" +
            "patientId='" + patientId + '\'' +
            ", mrn='" + mrn + '\'' +
            ", dob='" + dob + '\'' +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", sex=" + sex +
            ", phone='" + phone + '\'' +
            '}';
    }
}
