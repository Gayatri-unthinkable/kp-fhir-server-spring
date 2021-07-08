package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceRequestDto {


    @SerializedName("participants")
    @Expose
    private List<ParticipantOrderDto> participantOrder;

    @SerializedName("has_next_page")
    @Expose
    private boolean hasNextPage;
    @SerializedName("total_objects")
    @Expose
    private Integer totalObjects;

    public ServiceRequestDto() {
    }

    public ServiceRequestDto(List<ParticipantOrderDto> participantOrder, boolean hasNextPage, Integer totalObjects) {
        this.participantOrder = participantOrder;
        this.hasNextPage = hasNextPage;
        this.totalObjects = totalObjects;
    }

    public List<ParticipantOrderDto> getParticipantOrder() {
        return participantOrder;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public Integer getTotalObjects() {
        return totalObjects;
    }

    @Override
    public String toString() {
        return "ServiceRequestDto{" +
                "participantOrder=" + participantOrder +
                ", hasNextPage=" + hasNextPage +
                ", totalObjects=" + totalObjects +
                '}';
    }
}
