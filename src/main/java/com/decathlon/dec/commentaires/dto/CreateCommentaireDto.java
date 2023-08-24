package com.decathlon.dec.commentaires.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateCommentaireDto {

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;
   
    
}
