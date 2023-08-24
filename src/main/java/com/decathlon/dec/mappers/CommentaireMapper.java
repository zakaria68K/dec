package com.decathlon.dec.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.decathlon.dec.commentaires.dto.CreateCommentaireDto;
import com.decathlon.dec.commentaires.dto.EditCommentaireDto;
import com.decathlon.dec.commentaires.models.Commentaire;

@Mapper(componentModel = "spring")
public interface CommentaireMapper {
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target="publication", ignore= true)
    Commentaire createDtoToCommentaire(CreateCommentaireDto createCommentaireDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target="publication", ignore= true)
    void updateCommentaireFromDto(EditCommentaireDto editCommentaireDto, @MappingTarget Commentaire commentaire);

}
