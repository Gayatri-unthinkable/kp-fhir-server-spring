package com.hospital.kpfhirserver.dto;

import java.util.List;

public class PatientDto {

    private List<Object> recordings;
    private PatientProfileDto profile;

    public PatientDto(PatientProfileDto profile) {
        this.profile = profile;
    }

    public PatientDto(List<Object> recordings, PatientProfileDto profile) {
        this.recordings = recordings;
        this.profile = profile;
    }

    public List<Object> getRecordings() {
        return recordings;
    }

    public void setRecordings(List<Object> recordings) {
        this.recordings = recordings;
    }

    public PatientProfileDto getProfile() {
        return profile;
    }

    public void setProfile(PatientProfileDto profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "PatientDto{" +
            "recordings=" + recordings +
            ", profile=" + profile +
            '}';
    }
}
