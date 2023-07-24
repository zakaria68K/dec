package com.decathlon.dec.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.decathlon.dec.conges.dto.CreateCongeDto;
import com.decathlon.dec.conges.dto.UpdateCongeDto;
import com.decathlon.dec.conges.models.Conge;

@Mapper(componentModel = "spring")
public interface CongeDtoMapper {
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    @Mapping(target="status", ignore= true)
    Conge createDtoToConge(CreateCongeDto createCongeDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target="user", ignore= true)
    @Mapping(target="status", ignore= true)
    void updateCongeFromDto(UpdateCongeDto updateCongeDto, @MappingTarget Conge conge);


}
