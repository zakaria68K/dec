package com.decathlon.dec.conges.dto;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateCongeDto {

    @NotBlank(message = "La date de début est obligatoire")
    private Date startDate;
    @NotBlank(message = "La date de fin est obligatoire")
    private Date endDate;
    @NotBlank(message = "Le motif est obligatoire")
    private String reason;
  
    
}
