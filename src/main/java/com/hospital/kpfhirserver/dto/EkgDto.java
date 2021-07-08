package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EkgDto {

    @SerializedName("recordings")
    @Expose
    List<RecordingDto> recordingDtoList;

    public EkgDto() {
    }

    public EkgDto(List<RecordingDto> recordingDtoList) {
        this.recordingDtoList = recordingDtoList;
    }

    public List<RecordingDto> getRecordingDtoList() {
        return recordingDtoList;
    }

    public void setRecordingDtoList(List<RecordingDto> recordingDtoList) {
        this.recordingDtoList = recordingDtoList;
    }

    @Override
    public String toString() {
        return "EkgDto{" +
            "recordingDtoList=" + recordingDtoList +
            '}';
    }
}

