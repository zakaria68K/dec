package com.decathlon.dec.publications.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreatePublicationDto {

    @NotBlank(message = "la description est obligatoire")
    private String description;



}
