package com.hospital.kpfhirserver.dto;

import java.util.List;

public class RecordingDto {

    private String id;
    private String bpm;
    private String note;
    private String recordedAt;
    private List<MemberInterpretationDto> memberInterpretations;
    private boolean is6l;
    private PatientProfileDto participantProfile;
    private PreviewDataDto previewData;
    private Double previewStartSample;

    public RecordingDto() {
    }

    public RecordingDto(String id, String bpm, String note, String recordedAt,
                        List<MemberInterpretationDto> memberInterpretations, boolean is6l,
                        PatientProfileDto participantProfile, PreviewDataDto previewData,
                        Double previewStartSample) {
        this.id = id;
        this.bpm = bpm;
        this.note = note;
        this.recordedAt = recordedAt;
        this.memberInterpretations = memberInterpretations;
        this.is6l = is6l;
        this.participantProfile = participantProfile;
        this.previewData = previewData;
        this.previewStartSample = previewStartSample;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }

    public List<MemberInterpretationDto> getMemberInterpretations() {
        return memberInterpretations;
    }

    public void setMemberInterpretations(List<MemberInterpretationDto> memberInterpretations) {
        this.memberInterpretations = memberInterpretations;
    }

    public boolean isIs6l() {
        return is6l;
    }

    public void setIs6l(boolean is6l) {
        this.is6l = is6l;
    }

    public PatientProfileDto getParticipantProfile() {
        return participantProfile;
    }

    public void setParticipantProfile(PatientProfileDto participantProfile) {
        this.participantProfile = participantProfile;
    }

    public PreviewDataDto getPreviewData() {
        return previewData;
    }

    public void setPreviewData(PreviewDataDto previewData) {
        this.previewData = previewData;
    }

    public Double getPreviewStartSample() {
        return previewStartSample;
    }

    public void setPreviewStartSample(Double previewStartSample) {
        this.previewStartSample = previewStartSample;
    }

    @Override
    public String toString() {
        return "RecordingDto{" +
            "id='" + id + '\'' +
            ", bpm='" + bpm + '\'' +
            ", note='" + note + '\'' +
            ", recordedAt='" + recordedAt + '\'' +
            ", memberInterpretations=" + memberInterpretations +
            ", is6l=" + is6l +
            ", participantProfile=" + participantProfile +
            ", previewData=" + previewData +
            ", previewStartSample=" + previewStartSample +
            '}';
    }
}
