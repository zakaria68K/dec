package com.decathlon.dec.absences.dto;

import java.util.Date;

import jakarta.annotation.Nullable;

public class UpdateAbsenceDto {
    
    @Nullable
    private Date startDate;

    @Nullable
    private Date endDate;

    @Nullable
    private String reason;
}
