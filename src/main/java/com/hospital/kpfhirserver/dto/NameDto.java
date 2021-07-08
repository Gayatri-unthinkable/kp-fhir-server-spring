package com.hospital.kpfhirserver.dto;

import java.util.List;

public class NameDto {

    private String use;
    private String family;
    private List<String> given;

    public NameDto() {
    }

    public NameDto(String use, String family, List<String> given) {
        this.use = use;
        this.family = family;
        this.given = given;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public List<String> getGiven() {
        return given;
    }

    public void setGiven(List<String> given) {
        this.given = given;
    }

    @Override
    public String toString() {
        return "NameDto{" +
            "use='" + use + '\'' +
            ", family='" + family + '\'' +
            ", given=" + given +
            '}';
    }
}
