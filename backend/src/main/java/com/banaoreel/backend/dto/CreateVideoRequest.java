package com.banaoreel.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateVideoRequest {
    @NotBlank
    private String prompt;

    @Min(5) @Max(30)
    private int durationSec;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
}
