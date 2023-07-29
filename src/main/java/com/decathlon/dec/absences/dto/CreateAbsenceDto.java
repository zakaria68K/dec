package com.decathlon.dec.absences.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;

public class CreateAbsenceDto {
    
    @NotBlank(message = "La date de début est obligatoire")
    private Date startDate;
    @NotBlank(message = "La date de fin est obligatoire")
    private Date endDate;
    @NotBlank(message = "Le motif est obligatoire")
    private String reason;
    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
  
}
