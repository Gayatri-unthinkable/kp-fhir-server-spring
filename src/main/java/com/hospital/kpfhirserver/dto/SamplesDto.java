package com.hospital.kpfhirserver.dto;

import java.util.List;

public class SamplesDto {

    private List<Double> leadI;

    public SamplesDto() {
    }

    public SamplesDto(List<Double> leadI) {
        this.leadI = leadI;
    }

    public List<Double> getLeadI() {
        return leadI;
    }

    public void setLeadI(List<Double> leadI) {
        this.leadI = leadI;
    }
}
