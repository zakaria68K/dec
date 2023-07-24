package com.decathlon.dec.conges.dto;

import lombok.Builder;

import java.util.Date;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCongeDto {

    @Nullable
    private Date startDate;

    @Nullable
    private Date endDate;

    @Nullable
    private String reason;
    
}
