package com.hospital.kpfhirserver.dto;

public class PreviewDataDto {

    private String frequency;
    private String mainsFrequency;
    private String gain;
    private SamplesDto samples;

    public PreviewDataDto() {
    }

    public PreviewDataDto(String frequency, String mainsFrequency, String gain, SamplesDto samples) {
        this.frequency = frequency;
        this.mainsFrequency = mainsFrequency;
        this.gain = gain;
        this.samples = samples;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getMainsFrequency() {
        return mainsFrequency;
    }

    public void setMainsFrequency(String mainsFrequency) {
        this.mainsFrequency = mainsFrequency;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public SamplesDto getSamples() {
        return samples;
    }

    public void setSamples(SamplesDto samples) {
        this.samples = samples;
    }
}
