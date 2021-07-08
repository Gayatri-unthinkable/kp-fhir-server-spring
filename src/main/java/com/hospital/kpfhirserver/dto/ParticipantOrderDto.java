package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParticipantOrderDto {

    @SerializedName("id")
    @Expose
    private String patientId;

    @SerializedName("customParticipantID")
    @Expose
    private String mrn;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("enrollments")
    @Expose
    private List<EnrollmentDto> enrollmentDto;

    public ParticipantOrderDto() {
    }

    public ParticipantOrderDto(String patientId, String mrn,
                               String email, String firstName, String lastName, List<EnrollmentDto> enrollmentDto) {
        this.patientId = patientId;
        this.mrn = mrn;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enrollmentDto = enrollmentDto;
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

    public List<EnrollmentDto> getEnrollmentDto() {
        return enrollmentDto;
    }

    public void setEnrollmentDto(List<EnrollmentDto> enrollmentDto) {
        this.enrollmentDto = enrollmentDto;
    }

    @Override
    public String toString() {
        return "ParticipantOrderDto{" +
                "patientId='" + patientId + '\'' +
                ", mrn='" + mrn + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", enrollmentDto=" + enrollmentDto +
                '}';
    }
}
