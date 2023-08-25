package com.decathlon.dec.conges.dto;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateCongeDto {

    @NotNull(message = "La date de début est obligatoire")
    private Date startDate;
    @NotNull(message = "La date de fin est obligatoire")
    private Date endDate;
    @NotBlank(message = "Le motif est obligatoire")
    private String reason;
  
    
}
