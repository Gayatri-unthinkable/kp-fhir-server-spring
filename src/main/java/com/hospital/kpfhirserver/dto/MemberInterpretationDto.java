package com.hospital.kpfhirserver.dto;

import java.util.List;

public class MemberInterpretationDto {

    private String id;
    private String recordingId;
    private String teamId;
    private String memberId;
    private List<InterpretationDto> interpretations;

    public MemberInterpretationDto() {
    }

    public MemberInterpretationDto(String id, String recordingId, String teamId, String memberId,
                                   List<InterpretationDto> interpretations) {
        this.id = id;
        this.recordingId = recordingId;
        this.teamId = teamId;
        this.memberId = memberId;
        this.interpretations = interpretations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public List<InterpretationDto> getInterpretations() {
        return interpretations;
    }

    public void setInterpretations(List<InterpretationDto> interpretations) {
        this.interpretations = interpretations;
    }
}
