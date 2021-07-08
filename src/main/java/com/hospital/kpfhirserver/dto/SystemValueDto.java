package com.hospital.kpfhirserver.dto;

public class SystemValueDto {

    private String system;
    private String value;

    public SystemValueDto() {
    }

    public SystemValueDto(String system, String value) {
        this.system = system;
        this.value = value;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SystemValueDto{" +
            "system='" + system + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
