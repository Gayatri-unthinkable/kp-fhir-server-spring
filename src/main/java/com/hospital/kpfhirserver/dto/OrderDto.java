package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDto {


    // private PatientProfileDto patientProfileDto;
    @SerializedName("id")
    @Expose
    private String patientId;

    @SerializedName("customParticipantID")
    @Expose
    private String mrn;

    @SerializedName("enrollments")
    @Expose
    private List<EnrollmentDto> enrollmentDto;

    public OrderDto() {
    }


    public OrderDto(String patientId, String mrn, List<EnrollmentDto> enrollmentDto) {
        this.patientId = patientId;
        this.mrn = mrn;
        this.enrollmentDto = enrollmentDto;
    }

    public List<EnrollmentDto> getEnrollmentDto() {
        return enrollmentDto;
    }

    public void setEnrollmentDto(List<EnrollmentDto> enrollmentDto) {
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

    @Override
    public String toString() {
        return "OrderDto{" +
                "patientId='" + patientId + '\'' +
                ", mrn='" + mrn + '\'' +
                ", enrollmentDto=" + enrollmentDto +
                '}';
    }
}
