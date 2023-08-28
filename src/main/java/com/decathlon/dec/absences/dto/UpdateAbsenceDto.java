package com.decathlon.dec.absences.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.annotation.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAbsenceDto {
    
    @Nullable
    private Date startDate;

    @Nullable
    private Date endDate;

    @Nullable
    private String reason;
}
