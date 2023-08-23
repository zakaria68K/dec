package com.decathlon.dec.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.decathlon.dec.publications.dto.CreatePublicationDto;
import com.decathlon.dec.publications.dto.UpdatePublicatioDto;
import com.decathlon.dec.publications.models.Publication;

@Mapper(componentModel = "spring")
public abstract class PublicationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "user", ignore = true)
    public abstract Publication createDtoToPublication(CreatePublicationDto createPublicationDto);
    
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "user", ignore = true)
    public abstract void updatePublicationFromDto(UpdatePublicatioDto updatePublicationDto,
      @MappingTarget Publication publication);


    
}
