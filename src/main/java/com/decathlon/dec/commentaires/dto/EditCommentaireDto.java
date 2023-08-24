package com.decathlon.dec.commentaires.dto;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditCommentaireDto {
    
    @Nullable
    private String contenu;
}
