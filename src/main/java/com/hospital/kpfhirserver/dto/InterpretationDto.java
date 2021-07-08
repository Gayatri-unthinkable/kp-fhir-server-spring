package com.hospital.kpfhirserver.dto;

public class InterpretationDto {

    private String key;
    private String primary;
    private String severity;
    private String category;
    private String localizedDisplayText;

    public InterpretationDto() {
    }

    public InterpretationDto(String key, String primary, String severity, String category,
                             String localizedDisplayText) {
        this.key = key;
        this.primary = primary;
        this.severity = severity;
        this.category = category;
        this.localizedDisplayText = localizedDisplayText;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocalizedDisplayText() {
        return localizedDisplayText;
    }

    public void setLocalizedDisplayText(String localizedDisplayText) {
        this.localizedDisplayText = localizedDisplayText;
    }
}
