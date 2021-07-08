package com.hospital.kpfhirserver.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CodeDto {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("connectionTemplateID")
    @Expose
    private String connectionTemplateID;

    @SerializedName("participantID")
    @Expose
    private String participantID;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;


}
