package com.decathlon.dec.publications.dto;


import lombok.Builder;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePublicatioDto {

    @Nullable
    private String description;
    
}
