package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParticipantsDto {

    @SerializedName("participants")
    @Expose
    private List<PatientProfileDto> patientProfile;

    @SerializedName("has_next_page")
    @Expose
    private boolean hasNextPage;
    @SerializedName("total_objects")
    @Expose
    private Integer totalObjects;

    public ParticipantsDto() {
    }


    public List<PatientProfileDto> getPatientProfile() {
        return patientProfile;
    }

    public ParticipantsDto(List<PatientProfileDto> patientProfile, boolean hasNextPage, Integer totalObjects) {
        this.patientProfile = patientProfile;
        this.hasNextPage = hasNextPage;
        this.totalObjects = totalObjects;
    }

    public void setPatientProfile(List<PatientProfileDto> patientProfile) {
        this.patientProfile = patientProfile;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public Integer getTotalObjects() {
        return totalObjects;
    }

    public void setTotalObjects(Integer totalObjects) {
        this.totalObjects = totalObjects;
    }

    @Override
    public String toString() {
        return "ParticipantsDto{" +
                "patientProfile=" + patientProfile +
                ", hasNextPage=" + hasNextPage +
                ", totalObjects=" + totalObjects +
                '}';
    }
}